package com.example.computerstore.controller;

import com.example.computerstore.dto.UserDTO;
import com.example.computerstore.security.CurrentUser;
import com.example.computerstore.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/active")
    public List<UserDTO> getActive() {
        return service.getActive();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO dto) {
        UserDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        // Khách hàng/nhân viên chỉ được sửa hồ sơ của CHÍNH MÌNH; admin sửa được ai cũng được.
        CurrentUser.requireSelfOrAdmin(id);
        if (!CurrentUser.get().isAdmin()) {
            // Chặn leo thang đặc quyền: người tự sửa hồ sơ mình không được
            // nhét role/status khác vào body để tự phong ADMIN hoặc tự mở khóa.
            dto.setRole(null);
            dto.setStatus(null);
        }
        UserDTO updated = service.update(id, dto);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> setStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "ACTIVE");
        service.setStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
