package com.example.computerstore.config;

import com.example.computerstore.security.AuthTokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * PHÂN QUYỀN THẬT SỰ Ở BACKEND.
 *
 * Trước đây file này có `.anyRequest().permitAll()` — nghĩa là MỌI API đều
 * mở công khai bất kể giao diện có ẩn nút hay chặn route thế nào. Một nhân
 * viên (hoặc bất kỳ ai, kể cả không đăng nhập) chỉ cần gọi thẳng API bằng
 * Postman/DevTools là thao tác được như admin: xóa sản phẩm, khóa/mở tài
 * khoản khách hàng, tạo/xóa nhân viên khác...
 *
 * Bây giờ mỗi nhóm endpoint được khai rõ ai được phép gọi. Vài trường hợp
 * cần logic tinh hơn (vd: khách tự sửa hồ sơ CHÍNH MÌNH) được xử lý thêm
 * trong controller bằng CurrentUser.requireSelfOrAdmin(...).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthTokenFilter authTokenFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecurityConfig(AuthTokenFilter authTokenFilter) {
        this.authTokenFilter = authTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Preflight CORS luôn phải cho qua, nếu không trình duyệt sẽ chặn hết.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // --- Công khai: đăng nhập/đăng ký, ảnh sản phẩm ---
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()

                // --- Công khai: xem sản phẩm/danh mục/thương hiệu/CPU/RAM (khách duyệt web) ---
                .requestMatchers(HttpMethod.GET,
                        "/api/products/**", "/api/categories/**", "/api/brands/**",
                        "/api/cpus/**", "/api/rams/**").permitAll()

                // --- Công khai: đặt hàng (kể cả khách vãng lai không đăng nhập) ---
                .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/order-items").permitAll()

                // --- Tự sửa hồ sơ CHÍNH MÌNH: cho phép mọi role đã đăng nhập gọi PUT,
                // UserController sẽ tự kiểm tra "đúng là chính mình hoặc admin". ---
                .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()

                // --- Chỉ ADMIN: các thao tác quản lý tài khoản còn lại
                // (xem danh sách, tạo mới, xóa, khóa/mở khóa) ---
                .requestMatchers("/api/users/**").hasRole("ADMIN")

                // --- STAFF hoặc ADMIN: quản trị catalog (thêm/sửa) ---
                // XÓA catalog chỉ dành cho ADMIN — nhân viên không được phép xóa sản phẩm/danh mục/...
                .requestMatchers(HttpMethod.POST, "/api/products/**", "/api/categories/**",
                        "/api/brands/**", "/api/cpus/**", "/api/rams/**").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers(HttpMethod.PUT, "/api/products/**", "/api/categories/**",
                        "/api/brands/**", "/api/cpus/**", "/api/rams/**").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**", "/api/categories/**",
                        "/api/brands/**", "/api/cpus/**", "/api/rams/**").hasRole("ADMIN")

                // --- STAFF hoặc ADMIN: xem toàn bộ đơn hàng ---
                // XÓA đơn hàng chỉ ADMIN; nhân viên chỉ cập nhật trạng thái.
                .requestMatchers(HttpMethod.GET, "/api/orders", "/api/orders/active").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers(HttpMethod.DELETE, "/api/orders/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/order-items", "/api/order-items/{id}").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers(HttpMethod.DELETE, "/api/order-items/**").hasRole("ADMIN")

                // --- Đăng nhập rồi (bất kỳ role) mới được xem/hủy đơn của mình ---
                // (kiểm tra "đúng là đơn của mình" được làm thêm trong OrderController)
                .requestMatchers(HttpMethod.GET, "/api/orders/**", "/api/order-items/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/orders/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/orders/**").authenticated()

                .anyRequest().authenticated()
            )
            .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                        writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                                "Bạn cần đăng nhập để thực hiện thao tác này!"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        writeJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                                "Bạn không có quyền thực hiện thao tác này!"))
            )
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        return http.build();
    }

    private void writeJsonError(HttpServletResponse response, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
