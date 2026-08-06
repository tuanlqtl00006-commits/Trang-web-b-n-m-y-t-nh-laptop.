package com.example.computerstore.service;

import com.example.computerstore.model.OrderItem;
import com.example.computerstore.model.Order;
import com.example.computerstore.model.Product;
import com.example.computerstore.repository.OrderItemRepository;
import com.example.computerstore.repository.OrderRepository;
import com.example.computerstore.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {
    private final OrderItemRepository repository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemService(OrderItemRepository repository,
                             OrderRepository orderRepository,
                             ProductRepository productRepository) {
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<OrderItem> getAll() {
        return repository.findAll();
    }

    public List<OrderItem> getByOrderId(Long orderId) {
        return repository.findByOrderId(orderId);
    }

    public OrderItem getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public OrderItem create(OrderItem item) {
        if (item.getOrder() != null && item.getOrder().getId() != null) {
            Order order = orderRepository.findById(item.getOrder().getId()).orElse(null);
            item.setOrder(order);
        }
        if (item.getProduct() != null && item.getProduct().getId() != null) {
            Product product = productRepository.findById(item.getProduct().getId()).orElse(null);
            item.setProduct(product);
            if (product != null) {
                // Trừ kho khi tạo chi tiết đơn hàng
                int newStock = product.getStock() - item.getQuantity();
                product.setStock(Math.max(newStock, 0));
                productRepository.save(product);
            }
        }
        return repository.save(item);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
