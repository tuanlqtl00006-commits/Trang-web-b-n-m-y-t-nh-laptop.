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
 * Cấu hình bảo mật Spring Security:
 *
 * 1. Phân quyền:
 *    - Anonymous (không cần token): duyệt danh mục, sản phẩm, CPU, RAM, thương hiệu;
 *      đăng ký, đăng nhập; ĐẶT HÀNG (khách vãng lai cũng được đặt).
 *    - Đã đăng nhập (mọi role: CUSTOMER, STAFF, ADMIN): xem đơn hàng của mình,
 *      sửa thông tin cá nhân của mình.
 *    - STAFF & ADMIN: toàn bộ chức năng "bán hàng tại quầy", xem toàn bộ đơn hàng,
 *      cập nhật trạng thái đơn hàng (duyệt, giao hàng, hoàn thành, hủy), quản trị sản phẩm.
 *    - Chỉ ADMIN: quản lý tài khoản người dùng / nhân viên (xem, tạo, sửa, xóa, khóa/mở khóa),
 *      xóa đơn hàng, xóa danh mục / CPU / RAM / thương hiệu.
 *
 * 2. Xử lý lỗi: trả về JSON { "message": "..." } chuẩn tiếng Việt cho cả 401
 *    (chưa đăng nhập) và 403 (không đủ quyền), thay vì trang HTML trắng mặc định của Spring.
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
                // Cho phép pre-flight CORS OPTIONS request đi qua
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // --- Công khai: Auth + Ảnh tĩnh uploads ---
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

                // --- Chỉ ADMIN: các thao tác quản lý tài khoản còn lại ---
                .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/active", "/api/users/*").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers("/api/users/**").hasRole("ADMIN")

                // --- Chỉ ADMIN mới được thêm, sửa, xóa sản phẩm, danh mục, thương hiệu, CPU, RAM (nhân viên chỉ xem) ---
                .requestMatchers(HttpMethod.POST, "/api/products/**", "/api/categories/**",
                        "/api/brands/**", "/api/cpus/**", "/api/rams/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**", "/api/categories/**",
                        "/api/brands/**", "/api/cpus/**", "/api/rams/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**", "/api/categories/**",
                        "/api/brands/**", "/api/cpus/**", "/api/rams/**").hasRole("ADMIN")

                // --- STAFF hoặc ADMIN: xem toàn bộ đơn hàng ---
                .requestMatchers(HttpMethod.GET, "/api/orders", "/api/orders/active").hasAnyRole("ADMIN", "STAFF")
                // Ẩn / khôi phục / xóa đơn hàng chỉ ADMIN (STAFF không được phép ẩn/xóa)
                .requestMatchers(HttpMethod.PATCH, "/api/orders/*/hide", "/api/orders/*/unhide").hasRole("ADMIN")
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
                                "Bạn cần đăng nhập để thực hiện chức năng này!"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        writeJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                                "Bạn không có quyền thực hiện thao tác này!"))
            );

        return http.build();
    }

    private void writeJsonError(HttpServletResponse response, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("message", message);
        body.put("timestamp", System.currentTimeMillis());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
