package com.example.computerstore.service;

import com.example.computerstore.dto.LoginRequest;
import com.example.computerstore.dto.RegisterRequest;
import com.example.computerstore.dto.UserResponse;
import com.example.computerstore.model.User;
import com.example.computerstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent() && user.get().getPassword().equals(request.getPassword())) {
            User u = user.get();
            // Tài khoản bị khóa (INACTIVE) thì không cho đăng nhập
            if (u.getStatus() != null && u.getStatus().equalsIgnoreCase("INACTIVE")) {
                return null;
            }
            return new UserResponse(u.getId(), u.getFullname(), u.getEmail(), u.getPhone(), u.getRole(), u.getStatus());
        }
        return null;
    }

    public UserResponse register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return null;
        }

        User user = new User();
        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        user.setRole("USER");
        user.setStatus("ACTIVE");

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getFullname(), savedUser.getEmail(), 
                                savedUser.getPhone(), savedUser.getRole(), savedUser.getStatus());
    }
}
