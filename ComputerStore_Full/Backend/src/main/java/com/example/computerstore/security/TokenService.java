package com.example.computerstore.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Sinh và xác thực token đăng nhập kiểu "tự chế" bằng HMAC-SHA256, KHÔNG cần
 * thêm thư viện JWT ngoài.
 *
 * Định dạng token: base64url(payload) + "." + base64url(chữ ký HMAC của payload)
 * payload = "<userId>:<thoiDiemHetHan_epochMillis>"
 *
 * Vì chữ ký được server tự ký bằng secret riêng, client KHÔNG thể tự tạo hay
 * sửa token (vd đổi role, đổi id) mà không bị phát hiện — mọi request sẽ bị
 * AuthTokenFilter từ chối nếu chữ ký sai hoặc token đã hết hạn.
 *
 * Role không được nhúng vào token: mỗi request, filter sẽ tra lại role/trạng
 * thái MỚI NHẤT từ database theo userId trong token, để việc khóa tài khoản
 * hoặc đổi quyền có hiệu lực ngay lập tức mà không cần chờ token hết hạn.
 */
@Service
public class TokenService {

    private final Mac mac;

    // Thời hạn token: 12 giờ. Có thể chỉnh qua application.properties.
    @Value("${app.security.token-ttl-hours:12}")
    private long ttlHours;

    public TokenService(@Value("${app.security.token-secret:}") String configuredSecret) {
        String secret = (configuredSecret == null || configuredSecret.isBlank())
                // Fallback CHỈ dùng cho môi trường dev/demo khi chưa cấu hình.
                // Trong application.properties nên đặt app.security.token-secret
                // thành một chuỗi ngẫu nhiên dài, giữ bí mật.
                ? "dev-only-insecure-default-secret-please-override-in-application-properties"
                : configuredSecret;
        try {
            this.mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        } catch (Exception e) {
            throw new IllegalStateException("Không thể khởi tạo TokenService", e);
        }
    }

    public String generate(Long userId) {
        long expiry = System.currentTimeMillis() + ttlHours * 3600_000L;
        String payload = userId + ":" + expiry;
        String signature = sign(payload);
        return b64(payload) + "." + signature;
    }

    /**
     * Trả về userId nếu token hợp lệ (chữ ký đúng và chưa hết hạn),
     * ngược lại trả về null.
     */
    public Long verifyAndGetUserId(String token) {
        if (token == null || token.isBlank()) return null;
        int dot = token.indexOf('.');
        if (dot < 0) return null;

        String payloadB64 = token.substring(0, dot);
        String signature = token.substring(dot + 1);

        String payload;
        try {
            payload = new String(Base64.getUrlDecoder().decode(payloadB64), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }

        String expectedSignature = sign(payload);
        if (!constantTimeEquals(expectedSignature, signature)) {
            return null;
        }

        String[] parts = payload.split(":");
        if (parts.length != 2) return null;

        try {
            long expiry = Long.parseLong(parts[1]);
            if (System.currentTimeMillis() > expiry) return null;
            return Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private synchronized String sign(String payload) {
        byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
    }

    private static String b64(String s) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
