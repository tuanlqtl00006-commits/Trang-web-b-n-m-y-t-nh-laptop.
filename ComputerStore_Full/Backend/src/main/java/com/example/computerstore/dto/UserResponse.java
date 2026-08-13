package com.example.computerstore.dto;

public class UserResponse {
    private Long id;
    private String fullname;
    private String email;
    private String phone;
    private String role;
    private String status;
    private String token;

    public UserResponse() {
    }

    public UserResponse(Long id, String fullname, String email, String phone, String role, String status) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

    public UserResponse(Long id, String fullname, String email, String phone, String role, String status, String token) {
        this(id, fullname, email, phone, role, status);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
