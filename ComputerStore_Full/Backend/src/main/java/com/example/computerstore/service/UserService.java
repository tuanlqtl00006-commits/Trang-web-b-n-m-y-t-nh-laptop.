package com.example.computerstore.service;

import com.example.computerstore.dto.UserDTO;
import com.example.computerstore.model.User;
import com.example.computerstore.repository.UserRepository;
import com.example.computerstore.util.ValidationUtil;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<UserDTO> getAll() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getActive() {
        return repository.findByStatus("ACTIVE").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getById(Long id) {
        Optional<User> user = repository.findById(id);
        return user.map(this::convertToDTO).orElse(null);
    }

    public UserDTO create(UserDTO dto) {
        ValidationUtil.requireNotBlank(dto.getFullname(), "họ tên");
        ValidationUtil.requireValidEmail(dto.getEmail());
        ValidationUtil.requireValidPhone(dto.getPhone());
        ValidationUtil.requireNotBlank(dto.getPassword(), "mật khẩu");

        if (repository.findByEmail(dto.getEmail().trim()).isPresent()) {
            throw new IllegalArgumentException("Email đã được sử dụng, vui lòng chọn email khác!");
        }

        String role = dto.getRole() != null && !dto.getRole().trim().isEmpty()
                ? dto.getRole().trim().toUpperCase()
                : "USER";

        User user = new User();
        user.setFullname(dto.getFullname().trim());
        user.setEmail(dto.getEmail().trim());
        user.setPhone(dto.getPhone().trim());
        user.setPassword(dto.getPassword());
        user.setRole(role);
        user.setStatus("ACTIVE");

        User savedUser = repository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO update(Long id, UserDTO dto) {
        Optional<User> existing = repository.findById(id);
        if (existing.isPresent()) {
            User user = existing.get();

            ValidationUtil.requireNotBlank(dto.getFullname(), "họ tên");
            ValidationUtil.requireValidEmail(dto.getEmail());
            ValidationUtil.requireValidPhone(dto.getPhone());

            Optional<User> emailOwner = repository.findByEmail(dto.getEmail().trim());
            if (emailOwner.isPresent() && !emailOwner.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác!");
            }

            user.setFullname(dto.getFullname().trim());
            user.setEmail(dto.getEmail().trim());
            user.setPhone(dto.getPhone().trim());
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                user.setPassword(dto.getPassword());
            }
            if (dto.getRole() != null && !dto.getRole().trim().isEmpty()) {
                user.setRole(dto.getRole().trim().toUpperCase());
            }
            if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
                user.setStatus(dto.getStatus().trim().toUpperCase());
            }

            User updatedUser = repository.save(user);
            return convertToDTO(updatedUser);
        }
        return null;
    }

    public void delete(Long id) {
        Optional<User> existing = repository.findById(id);
        if (existing.isPresent()) {
            User user = existing.get();
            user.setStatus("INACTIVE");
            repository.save(user);
        }
    }

    public void setStatus(Long id, String status) {
        Optional<User> existing = repository.findById(id);
        if (existing.isPresent()) {
            User user = existing.get();
            user.setStatus(status);
            repository.save(user);
        }
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getFullname(), user.getEmail(),
                           user.getPhone(), user.getPassword(), user.getRole(), user.getStatus());
    }
}
