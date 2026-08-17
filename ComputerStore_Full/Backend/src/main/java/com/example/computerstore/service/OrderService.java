package com.example.computerstore.service;

import com.example.computerstore.model.Order;
import com.example.computerstore.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Thứ tự chuyển trạng thái chuẩn của đơn hàng.
     * Khách hàng: PENDING -> CANCELLED (tự hủy khi chưa duyệt).
     * Nhân viên/Admin: PENDING -> CONFIRMED -> SHIPPING -> COMPLETED, hoặc PENDING/CONFIRMED -> CANCELLED.
     */
    public static final List<String> STATUS_FLOW = List.of(
            "PENDING",      // Chờ xác nhận
            "CONFIRMED",    // Đã xác nhận (chuẩn bị giao)
            "SHIPPING",     // Đang giao hàng
            "COMPLETED"     // Đã giao thành công / hoàn tất
    );

    public static final String STATUS_CANCELLED = "CANCELLED";

    public List<Order> getAll() {
        return repository.findByIsHiddenFalseOrIsHiddenNull();
    }

    public List<Order> getAll(boolean showHidden) {
        if (showHidden) {
            return repository.findByIsHiddenTrue();
        }
        return repository.findByIsHiddenFalseOrIsHiddenNull();
    }

    public Order hide(Long id, Long hiddenBy) {
        Optional<Order> existing = repository.findById(id);
        if (existing.isPresent()) {
            Order o = existing.get();
            o.setIsHidden(true);
            o.setHiddenAt(java.time.LocalDateTime.now());
            o.setHiddenBy(hiddenBy);
            return repository.save(o);
        }
        return null;
    }

    public Order unhide(Long id) {
        Optional<Order> existing = repository.findById(id);
        if (existing.isPresent()) {
            Order o = existing.get();
            o.setIsHidden(false);
            o.setHiddenAt(null);
            o.setHiddenBy(null);
            return repository.save(o);
        }
        return null;
    }

    public List<Order> getActive() {
        return repository.findAll();
    }

    public Order getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Order> getByStatus(String status) {
        return repository.findByStatus(status);
    }

    public List<Order> getByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public Order create(Order order) {
        if (order.getStatus() == null || order.getStatus().trim().isEmpty()) {
            order.setStatus("PENDING");
        }
        if (order.getOrderDate() == null) {
            order.setOrderDate(java.time.LocalDateTime.now());
        }
        return repository.save(order);
    }

    public Order update(Long id, Order order) {
        Optional<Order> existing = repository.findById(id);
        if (existing.isPresent()) {
            Order o = existing.get();
            if (order.getCustomerName() != null) o.setCustomerName(order.getCustomerName());
            if (order.getPhone() != null) o.setPhone(order.getPhone());
            if (order.getAddress() != null) o.setAddress(order.getAddress());
            if (order.getPaymentMethod() != null) o.setPaymentMethod(order.getPaymentMethod());
            if (order.getSubtotal() != null) o.setSubtotal(order.getSubtotal());
            if (order.getVat() != null) o.setVat(order.getVat());
            if (order.getTotal() != null) o.setTotal(order.getTotal());
            if (order.getStatus() != null) o.setStatus(order.getStatus());
            if (order.getCancellationReason() != null) o.setCancellationReason(order.getCancellationReason());
            return repository.save(o);
        }
        return null;
    }

    /**
     * Cập nhật trạng thái đơn hàng theo luồng nghiệp vụ.
     */
    public Order updateStatus(Long id, String nextStatus, String cancellationReason) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng #" + id));

        String currentStatus = order.getStatus();

        if (STATUS_CANCELLED.equalsIgnoreCase(nextStatus)) {
            if ("COMPLETED".equalsIgnoreCase(currentStatus)) {
                throw new IllegalStateException("Đơn hàng đã hoàn thành, không thể hủy!");
            }
            if (STATUS_CANCELLED.equalsIgnoreCase(currentStatus)) {
                throw new IllegalStateException("Đơn hàng này đã bị hủy trước đó!");
            }
            order.setStatus(STATUS_CANCELLED);
            order.setCancellationReason(cancellationReason != null && !cancellationReason.isBlank()
                    ? cancellationReason
                    : "Hủy theo yêu cầu");
            return repository.save(order);
        }

        order.setStatus(nextStatus);
        return repository.save(order);
    }

    public void delete(Long id) {
        hide(id, null);
    }
}
