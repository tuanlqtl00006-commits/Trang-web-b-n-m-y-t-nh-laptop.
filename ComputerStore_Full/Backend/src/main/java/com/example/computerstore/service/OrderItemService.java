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
        if (item.getQuantity() == null || item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm phải lớn hơn 0");
        }

        if (item.getOrder() != null && item.getOrder().getId() != null) {
            Order order = orderRepository.findById(item.getOrder().getId()).orElse(null);
            item.setOrder(order);
        }
        if (item.getProduct() != null && item.getProduct().getId() != null) {
            Product product = productRepository.findById(item.getProduct().getId()).orElse(null);
            if (product == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại");
            }
            item.setProduct(product);
            // Luôn lấy giá từ product.getPrice() trong DB để gán vào item
            item.setPrice(product.getPrice());

            // Trước khi trừ kho, kiểm tra product.getStock() >= item.getQuantity()
            int currentStock = product.getStock() != null ? product.getStock() : 0;
            if (currentStock < item.getQuantity()) {
                throw new IllegalArgumentException("Không đủ hàng trong kho");
            }

            // Trừ kho khi tạo chi tiết đơn hàng
            product.setStock(currentStock - item.getQuantity());
            productRepository.save(product);
        } else {
            throw new IllegalArgumentException("Sản phẩm không hợp lệ");
        }
        return repository.save(item);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
