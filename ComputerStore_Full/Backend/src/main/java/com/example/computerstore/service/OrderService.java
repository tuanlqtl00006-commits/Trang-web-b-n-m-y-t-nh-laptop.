package com.example.computerstore.service;

import com.example.computerstore.model.Order;
import com.example.computerstore.repository.OrderRepository;
import com.example.computerstore.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final UserRepository userRepository;

    // Allowed forward transitions for the order status workflow:
    // PENDING (chờ admin/nhân viên xác nhận)
    //   -> CONFIRMED (admin/nhân viên xác nhận đơn)
    //   -> CANCELLED (khách hàng hủy khi chưa giao)
    // CONFIRMED
    //   -> SHIPPING (admin/nhân viên giao cho đơn vị vận chuyển)
    //   -> CANCELLED
    // SHIPPING
    //   -> COMPLETED (khách hàng xác nhận đã nhận hàng & thanh toán)
    // COMPLETED / CANCELLED: trạng thái cuối, không đổi được nữa.
    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            "PENDING", Set.of("CONFIRMED", "CANCELLED"),
            "CONFIRMED", Set.of("SHIPPING", "CANCELLED"),
            "SHIPPING", Set.of("COMPLETED", "CANCELLED"),
            "COMPLETED", Set.of(),
            "CANCELLED", Set.of()
    );

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
            return repository.save(o);
        }
        return null;
    }

    /**
     * Guided status transition used by both the admin/staff Orders screen and the
     * customer order-detail screen. Enforces the workflow above instead of allowing
     * an arbitrary jump to any status.
     */
    public Order updateStatus(Long id, String newStatus, String cancellationReason) {
        Optional<Order> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng!");
        }
        Order o = existing.get();
        String current = o.getStatus() != null ? o.getStatus().toUpperCase() : "PENDING";
        String target = newStatus != null ? newStatus.toUpperCase() : "";

        Set<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(target)) {
            throw new IllegalArgumentException(
                    "Không thể chuyển trạng thái đơn hàng từ " + current + " sang " + target + "!");
        }

        o.setStatus(target);
        if ("CANCELLED".equals(target)) {
            o.setCancellationReason(cancellationReason != null && !cancellationReason.isBlank()
                    ? cancellationReason
                    : "Không có lý do cụ thể");
        }
        return repository.save(o);
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
