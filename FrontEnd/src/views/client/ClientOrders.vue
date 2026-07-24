<template>
  <div>
    <h4 class="fw-bold mb-4"><i class="bi bi-bag-check me-2"></i>Đơn hàng của tôi</h4>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border" role="status"></div>
    </div>

    <div v-else-if="orders.length === 0" class="text-center text-muted py-5">
      Bạn chưa có đơn hàng nào.
    </div>

    <table v-else class="table align-middle">
      <thead>
        <tr>
          <th>Mã đơn</th>
          <th>Ngày đặt</th>
          <th>Tổng tiền</th>
          <th>Trạng thái</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="o in orders" :key="o.id">
          <td>#{{ o.id }}</td>
          <td>{{ formatDate(o.orderDate) }}</td>
          <td class="price-text">{{ formatPrice(o.total) }}₫</td>
          <td>
            <span class="badge" :class="statusClass(o.status)">
              {{ statusLabel(o.status) }}
            </span>
          </td>
          <td>
            <router-link :to="`/order-detail/${o.id}`" class="btn btn-sm btn-outline-dark">
              Chi tiết
            </router-link>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { getOrdersByUser } from "@/api/order";

export default {
  name: "ClientOrders",
  data() {
    return {
      orders: [],
      loading: true,
    };
  },
  async mounted() {
    const user = JSON.parse(localStorage.getItem("user") || "null");
    if (!user) {
      this.$router.push("/login");
      return;
    }
    try {
      const res = await getOrdersByUser(user.id);
      this.orders = res.data.sort((a, b) => b.id - a.id);
    } catch (e) {
      console.error("Lỗi tải đơn hàng:", e);
    } finally {
      this.loading = false;
    }
  },
  methods: {
    formatPrice(price) {
      if (!price) return "0";
      return price.toLocaleString("vi-VN");
    },
    formatDate(date) {
      if (!date) return "—";
      return new Date(date).toLocaleString("vi-VN");
    },
    statusLabel(status) {
      const map = {
        PENDING: "Chờ xác nhận",
        CONFIRMED: "Đã xác nhận",
        SHIPPING: "Đang giao",
        COMPLETED: "Hoàn thành",
        CANCELLED: "Đã hủy",
      };
      return map[status] || status;
    },
    statusClass(status) {
      const map = {
        PENDING: "badge-status-pending",
        CONFIRMED: "badge-status-confirmed",
        SHIPPING: "badge-status-shipping",
        COMPLETED: "badge-status-completed",
        CANCELLED: "badge-status-cancelled",
      };
      return map[status] || "bg-secondary";
    },
  },
};
</script>
