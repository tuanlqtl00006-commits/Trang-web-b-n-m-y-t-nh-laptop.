package com.example.computerstore.controller;

import com.example.computerstore.model.Order;
import com.example.computerstore.model.OrderItem;
import com.example.computerstore.security.CurrentUser;
import com.example.computerstore.service.OrderItemService;
import com.example.computerstore.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@CrossOrigin("*")
public class OrderItemController {
    private final OrderItemService service;
    private final OrderService orderService;

    public OrderItemController(OrderItemService service, OrderService orderService) {
        this.service = service;
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderItem> getAll() {
        return service.getAll();
    }

    @GetMapping("/order/{orderId}")
    public List<OrderItem> getByOrderId(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        requireOwnerOrStaff(order);
        return service.getByOrderId(orderId);
    }

    @GetMapping("/{id}")
    public OrderItem getById(@PathVariable Long id) {
        OrderItem item = service.getById(id);
        if (item != null) {
            requireOwnerOrStaff(item.getOrder());
        }
        return item;
    }

    @PostMapping
    public ResponseEntity<OrderItem> create(@RequestBody OrderItem item) {
        OrderItem created = service.create(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void requireOwnerOrStaff(Order order) {
        if (order == null) return;
        Long ownerId = order.getUser() != null ? order.getUser().getId() : null;
        if (ownerId == null) {
            CurrentUser.requireSelfOrStaff(-1L);
        } else {
            CurrentUser.requireSelfOrStaff(ownerId);
        }
    }
}

