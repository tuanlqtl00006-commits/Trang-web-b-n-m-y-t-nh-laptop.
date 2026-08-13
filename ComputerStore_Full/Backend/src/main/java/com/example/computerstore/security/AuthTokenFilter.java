package com.example.computerstore.security;

import com.example.computerstore.model.User;
import com.example.computerstore.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Đọc header "Authorization: Bearer <token>" trên MỖI request.
 *
 * Nếu token hợp lệ: tra lại User theo id trong token TRỰC TIẾP TỪ DATABASE
 * (không tin bất cứ role/trạng thái nào client tự gửi lên), rồi gắn
 * Authentication (kèm role hiện tại) vào SecurityContext để
 * SecurityConfig/@PreAuthorize phía sau dùng để phân quyền.
 *
 * Nếu không có token, token sai, hoặc tài khoản đã bị khóa (INACTIVE) thì
 * coi như request ẩn danh — các endpoint yêu cầu đăng nhập/role sẽ tự động
 * bị SecurityConfig chặn (401/403) ở bước sau.
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public AuthTokenFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            Long userId = tokenService.verifyAndGetUserId(token);

            if (userId != null) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    boolean locked = user.getStatus() != null && user.getStatus().equalsIgnoreCase("INACTIVE");
                    if (!locked) {
                        UserPrincipal principal = new UserPrincipal(user.getId(), user.getRole());
                        String roleAuthority = "ROLE_" + principal.getRole();
                        var authToken = new UsernamePasswordAuthenticationToken(
                                principal, null, List.of(new SimpleGrantedAuthority(roleAuthority)));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
