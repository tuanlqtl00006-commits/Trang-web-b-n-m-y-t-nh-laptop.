package com.example.computerstore.service;

import com.example.computerstore.model.Order;
import com.example.computerstore.repository.OrderRepository;
import com.example.computerstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<Order> getAll() {
        return repository.findAll();
    }

    public List<Order> getActive() {
        return repository.findByStatus("ACTIVE");
    }

    public Order getById(Long id) {
        Optional<Order> order = repository.findById(id);
        return order.orElse(null);
    }

    public List<Order> getByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public Order create(Order order) {
        order.setStatus("PENDING");
        return repository.save(order);
    }

    public Order update(Long id, Order order) {
        Optional<Order> existing = repository.findById(id);
        if (existing.isPresent()) {
            Order o = existing.get();
            o.setCustomerName(order.getCustomerName());
            o.setPhone(order.getPhone());
            o.setAddress(order.getAddress());
            o.setPaymentMethod(order.getPaymentMethod());
            o.setSubtotal(order.getSubtotal());
            o.setVat(order.getVat());
            o.setTotal(order.getTotal());
            o.setStatus(order.getStatus());
            o.setOrderDate(order.getOrderDate());
            o.setCancellationReason(order.getCancellationReason());
            return repository.save(o);
        }
        return null;
    }

    public void delete(Long id) {
        Optional<Order> existing = repository.findById(id);
        if (existing.isPresent()) {
            Order o = existing.get();
            o.setStatus("INACTIVE");
            repository.save(o);
        }
    }
}
