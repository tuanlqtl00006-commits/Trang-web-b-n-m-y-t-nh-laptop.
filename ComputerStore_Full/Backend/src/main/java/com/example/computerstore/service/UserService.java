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

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getFullname(),
            user.getEmail(),
            user.getPhone(),
            null, // Không trả password ra DTO
            user.getRole(),
            user.getStatus()
        );
    }

    private User convertToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setFullname(dto.getFullname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setStatus(dto.getStatus());
        return user;
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
        return repository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public UserDTO create(UserDTO dto) {
        ValidationUtil.requireNotBlank(dto.getFullname(), "họ tên");
        ValidationUtil.requireValidEmail(dto.getEmail());
        ValidationUtil.requireValidPhone(dto.getPhone());
        ValidationUtil.requireNotBlank(dto.getPassword(), "mật khẩu");

        Optional<User> existing = repository.findByEmail(dto.getEmail().trim());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email đã được sử dụng, vui lòng chọn email khác!");
        }

        User user = convertToEntity(dto);
        user.setFullname(dto.getFullname().trim());
        user.setEmail(dto.getEmail().trim());
        user.setPhone(dto.getPhone().trim());
        if (user.getStatus() == null || user.getStatus().trim().isEmpty()) {
            user.setStatus("ACTIVE");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
        }
        User savedUser = repository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO update(Long id, UserDTO dto) {
        Optional<User> existing = repository.findById(id);
        if (existing.isPresent()) {
            User user = existing.get();
            if (dto.getFullname() != null && !dto.getFullname().trim().isEmpty()) {
                user.setFullname(dto.getFullname().trim());
            }
            if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
                String newEmail = dto.getEmail().trim();
                ValidationUtil.requireValidEmail(newEmail);
                if (!newEmail.equalsIgnoreCase(user.getEmail())) {
                    Optional<User> duplicate = repository.findByEmail(newEmail);
                    if (duplicate.isPresent()) {
                        throw new IllegalArgumentException("Email đã được sử dụng bởi tài khoản khác!");
                    }
                    user.setEmail(newEmail);
                }
            }
            if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
                ValidationUtil.requireValidPhone(dto.getPhone().trim());
                user.setPhone(dto.getPhone().trim());
            }
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
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
}
