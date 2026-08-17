package com.example.computerstore.controller;

import com.example.computerstore.model.Order;
import com.example.computerstore.security.CurrentUser;
import com.example.computerstore.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<Order> getAll(@RequestParam(value = "showHidden", required = false, defaultValue = "false") boolean showHidden) {
        return service.getAll(showHidden);
    }

    @GetMapping("/active")
    public List<Order> getActive() {
        return service.getActive();
    }

    @GetMapping("/{id}")
    public Order getById(@PathVariable Long id) {
        Order order = service.getById(id);
        requireOwnerOrStaff(order);
        return order;
    }

    @GetMapping("/user/{userId}")
    public List<Order> getByUserId(@PathVariable Long userId) {
        // Khách chỉ xem được đơn của CHÍNH MÌNH; staff/admin xem được của ai cũng được.
        CurrentUser.requireSelfOrStaff(userId);
        return service.getByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        Order created = service.create(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order order) {
        requireOwnerOrStaff(service.getById(id));
        Order updated = service.update(id, order);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Guided status change: PENDING -> CONFIRMED -> SHIPPING -> COMPLETED, or -> CANCELLED.
     * Body: { "status": "CONFIRMED", "cancellationReason": "..." (only needed when cancelling) }
     * Khách hàng dùng API này để tự hủy/xác nhận nhận hàng cho đơn của CHÍNH MÌNH.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        requireOwnerOrStaff(service.getById(id));
        Order updated = service.updateStatus(id, body.get("status"), body.get("cancellationReason"));
        return ResponseEntity.ok(updated);
    }

    /**
     * Đơn của khách vãng lai (không tài khoản) không có chủ -> chỉ staff/admin
     * mới xem/sửa được. Đơn có chủ -> chính chủ hoặc staff/admin.
     */
    private void requireOwnerOrStaff(Order order) {
        if (order == null) return;
        Long ownerId = order.getUser() != null ? order.getUser().getId() : null;
        if (ownerId == null) {
            CurrentUser.requireSelfOrStaff(-1L); // không ai "là chính chủ" đơn khách vãng lai
        } else {
            CurrentUser.requireSelfOrStaff(ownerId);
        }
    }

    @PatchMapping("/{id}/hide")
    public ResponseEntity<Order> hide(@PathVariable Long id) {
        if (!CurrentUser.get().isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Chỉ ADMIN mới có quyền ẩn đơn hàng!");
        }
        Order hidden = service.hide(id, CurrentUser.get().getId());
        if (hidden != null) {
            return ResponseEntity.ok(hidden);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/unhide")
    public ResponseEntity<Order> unhide(@PathVariable Long id) {
        if (!CurrentUser.get().isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Chỉ ADMIN mới có quyền khôi phục đơn hàng!");
        }
        Order unhidden = service.unhide(id);
        if (unhidden != null) {
            return ResponseEntity.ok(unhidden);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!CurrentUser.get().isAdmin()) {
            throw new org.springframework.security.access.AccessDeniedException("Chỉ ADMIN mới có quyền thao tác trên đơn hàng!");
        }
        service.hide(id, CurrentUser.get().getId());
        return ResponseEntity.noContent().build();
    }
}
