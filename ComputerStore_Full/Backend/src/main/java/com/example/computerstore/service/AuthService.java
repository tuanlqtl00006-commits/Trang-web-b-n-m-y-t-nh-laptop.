package com.example.computerstore.service;

import com.example.computerstore.dto.LoginRequest;
import com.example.computerstore.dto.RegisterRequest;
import com.example.computerstore.dto.UserResponse;
import com.example.computerstore.model.User;
import com.example.computerstore.repository.UserRepository;
import com.example.computerstore.security.TokenService;
import com.example.computerstore.util.ValidationUtil;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    /**
     * Returns the logged-in user, or throws IllegalArgumentException with a
     * user-facing message describing exactly why the login failed
     * (wrong credentials vs. disabled account) so all three roles
     * (admin / staff / customer) get clear, correct feedback.
     */
    public UserResponse login(LoginRequest request) {
        if (ValidationUtil.isBlank(request.getEmail()) || ValidationUtil.isBlank(request.getPassword())) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ email và mật khẩu!");
        }

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail().trim());
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng!");
        }

        User u = userOpt.get();
        if (u.getStatus() != null && u.getStatus().equalsIgnoreCase("INACTIVE")) {
            throw new IllegalArgumentException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên!");
        }

        String token = tokenService.generate(u.getId());
        return new UserResponse(u.getId(), u.getFullname(), u.getEmail(), u.getPhone(), u.getRole(), u.getStatus(), token);
    }

    public UserResponse register(RegisterRequest request) {
        ValidationUtil.requireNotBlank(request.getFullname(), "họ tên");
        ValidationUtil.requireValidEmail(request.getEmail());
        ValidationUtil.requireValidPhone(request.getPhone());
        ValidationUtil.requireNotBlank(request.getPassword(), "mật khẩu");

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail().trim());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email đã được sử dụng, vui lòng chọn email khác!");
        }

        User user = new User();
        user.setFullname(request.getFullname().trim());
        user.setEmail(request.getEmail().trim());
        user.setPhone(request.getPhone().trim());
        user.setPassword(request.getPassword());
        user.setRole("USER");
        user.setStatus("ACTIVE");

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getFullname(), savedUser.getEmail(),
                                savedUser.getPhone(), savedUser.getRole(), savedUser.getStatus());
    }
}
