<template>
  <div>
    <h4 class="fw-bold mb-4">Tổng quan</h4>

    <div class="row g-3 mb-4">
      <div class="col-md-3">
        <div class="card p-3 text-center">
          <i class="bi bi-laptop fs-2 text-primary"></i>
          <h3 class="fw-bold mt-2">{{ stats.products }}</h3>
          <p class="text-muted mb-0">Sản phẩm</p>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card p-3 text-center">
          <i class="bi bi-bag-check fs-2 text-success"></i>
          <h3 class="fw-bold mt-2">{{ stats.orders }}</h3>
          <p class="text-muted mb-0">Đơn hàng</p>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card p-3 text-center">
          <i class="bi bi-people fs-2 text-info"></i>
          <h3 class="fw-bold mt-2">{{ stats.customers }}</h3>
          <p class="text-muted mb-0">Khách hàng</p>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card p-3 text-center">
          <i class="bi bi-cash-coin fs-2 text-danger"></i>
          <h3 class="fw-bold mt-2">{{ formatPrice(stats.revenue) }}₫</h3>
          <p class="text-muted mb-0">Doanh thu (hoàn thành)</p>
        </div>
      </div>
    </div>

    <div class="card p-3">
      <h6 class="fw-bold mb-3">Đơn hàng gần đây</h6>
      <table class="table align-middle mb-0">
        <thead>
          <tr>
            <th>Mã đơn</th>
            <th>Khách hàng</th>
            <th>Tổng tiền</th>
            <th>Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in recentOrders" :key="o.id">
            <td>#{{ o.id }}</td>
            <td>{{ o.customerName }}</td>
            <td class="price-text">{{ formatPrice(o.total) }}₫</td>
            <td>
              <span class="badge" :class="statusClass(o.status)">{{ statusLabel(o.status) }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { getProducts } from "@/api/product";
import { getOrders } from "@/api/order";
import { getUsers } from "@/api/user";

export default {
  name: "AdminDashboard",
  data() {
    return {
      stats: { products: 0, orders: 0, customers: 0, revenue: 0 },
      recentOrders: [],
    };
  },
  async mounted() {
    const [pRes, oRes, uRes] = await Promise.all([getProducts(), getOrders(), getUsers()]);
    this.stats.products = pRes.data.length;
    this.stats.orders = oRes.data.length;
    this.stats.customers = uRes.data.filter((u) => !u.role || u.role.toLowerCase() !== "admin").length;
    this.stats.revenue = oRes.data
      .filter((o) => o.status === "COMPLETED")
      .reduce((s, o) => s + (o.total || 0), 0);
    this.recentOrders = [...oRes.data].sort((a, b) => b.id - a.id).slice(0, 5);
  },
  methods: {
    formatPrice(price) {
      if (!price) return "0";
      return price.toLocaleString("vi-VN");
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
