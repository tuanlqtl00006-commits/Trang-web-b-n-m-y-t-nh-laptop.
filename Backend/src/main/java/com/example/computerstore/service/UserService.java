package com.example.computerstore.service;

import com.example.computerstore.dto.UserDTO;
import com.example.computerstore.model.User;
import com.example.computerstore.repository.UserRepository;
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
        User user = new User();
        user.setFullname(dto.getFullname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole() != null ? dto.getRole() : "USER");
        user.setStatus("ACTIVE");

        User savedUser = repository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO update(Long id, UserDTO dto) {
        Optional<User> existing = repository.findById(id);
        if (existing.isPresent()) {
            User user = existing.get();
            user.setFullname(dto.getFullname());
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            if (dto.getPassword() != null) {
                user.setPassword(dto.getPassword());
            }
            user.setRole(dto.getRole());
            user.setStatus(dto.getStatus());

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

    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getFullname(), user.getEmail(),
                           user.getPhone(), user.getPassword(), user.getRole(), user.getStatus());
    }
}
