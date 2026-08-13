package com.example.computerstore.security;

/**
 * Thông tin người dùng hiện tại, được AuthTokenFilter tra cứu MỚI NHẤT từ
 * database (không lấy từ token) rồi gắn vào SecurityContext cho mỗi request.
 */
public class UserPrincipal {
    private final Long id;
    private final String role; // "ADMIN" | "STAFF" | "USER"

    public UserPrincipal(Long id, String role) {
        this.id = id;
        this.role = role == null ? "" : role.toUpperCase();
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isStaff() {
        return "STAFF".equals(role);
    }

    public boolean isAdminOrStaff() {
        return isAdmin() || isStaff();
    }

    public boolean isSelf(Long userId) {
        return id != null && id.equals(userId);
    }
}
