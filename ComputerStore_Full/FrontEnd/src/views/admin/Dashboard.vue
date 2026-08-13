<template>
  <div>
    <div class="d-flex align-items-center justify-content-between mb-4">
      <h4 class="fw-bold mb-0">Tổng quan</h4>
      <span v-if="!isAdmin" class="badge bg-info text-dark fs-6 px-3 py-2">
        <i class="bi bi-person-badge me-1"></i>Chế độ nhân viên
      </span>
    </div>

    <!-- Thẻ thống kê -->
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
      <!-- Chỉ admin mới thấy số khách hàng & doanh thu -->
      <template v-if="isAdmin">
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
      </template>

      <!-- Nhân viên: thay bằng đơn hàng hôm nay -->
      <template v-else>
        <div class="col-md-3">
          <div class="card p-3 text-center">
            <i class="bi bi-calendar-check fs-2 text-warning"></i>
            <h3 class="fw-bold mt-2">{{ stats.todayOrders }}</h3>
            <p class="text-muted mb-0">Đơn hôm nay</p>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card p-3 text-center">
            <i class="bi bi-hourglass-split fs-2 text-secondary"></i>
            <h3 class="fw-bold mt-2">{{ stats.pendingOrders }}</h3>
            <p class="text-muted mb-0">Chờ xử lý</p>
          </div>
        </div>
      </template>
    </div>

    <!-- Bảng đơn hàng gần đây -->
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
          <tr v-if="recentOrders.length === 0">
            <td colspan="4" class="text-center text-muted py-3">Chưa có đơn hàng nào.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Nhân viên: gợi ý chức năng nhanh -->
    <div v-if="!isAdmin" class="row g-3 mt-1">
      <div class="col-12">
        <div class="card p-3 border-info">
          <h6 class="fw-bold mb-3 text-info"><i class="bi bi-lightning-charge me-1"></i>Chức năng nhanh</h6>
          <div class="d-flex gap-2 flex-wrap">
            <router-link to="/admin/sales" class="btn btn-primary">
              <i class="bi bi-cash-coin me-1"></i>Bán hàng tại quầy
            </router-link>
            <router-link to="/admin/orders" class="btn btn-outline-dark">
              <i class="bi bi-bag-check me-1"></i>Quản lý đơn hàng
            </router-link>
            <router-link to="/admin/products" class="btn btn-outline-dark">
              <i class="bi bi-laptop me-1"></i>Sản phẩm
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getProducts } from "@/api/product";
import { getOrders } from "@/api/order";
import { getUsers } from "@/api/user";
import { getUser } from "@/utils/session";

export default {
  name: "AdminDashboard",
  data() {
    return {
      stats: { products: 0, orders: 0, customers: 0, revenue: 0, todayOrders: 0, pendingOrders: 0 },
      recentOrders: [],
    };
  },
  computed: {
    isAdmin() {
      return (getUser()?.role || "").toLowerCase() === "admin";
    },
  },
  async mounted() {
    if (this.isAdmin) {
      const [pRes, oRes, uRes] = await Promise.all([getProducts(), getOrders(), getUsers()]);
      this.stats.products = pRes.data.length;
      this.stats.orders = oRes.data.length;
      this.stats.customers = uRes.data.filter((u) => !u.role || u.role.toLowerCase() === "customer").length;
      this.stats.revenue = oRes.data
        .filter((o) => o.status === "COMPLETED")
        .reduce((s, o) => s + (o.total || 0), 0);
      this.recentOrders = [...oRes.data].sort((a, b) => b.id - a.id).slice(0, 5);
    } else {
      // Nhân viên: chỉ tải sản phẩm và đơn hàng, không tải dữ liệu khách hàng
      const [pRes, oRes] = await Promise.all([getProducts(), getOrders()]);
      const today = new Date().toDateString();
      this.stats.products = pRes.data.length;
      this.stats.orders = oRes.data.length;
      this.stats.todayOrders = oRes.data.filter(
        (o) => new Date(o.orderDate).toDateString() === today
      ).length;
      this.stats.pendingOrders = oRes.data.filter((o) => o.status === "PENDING").length;
      this.recentOrders = [...oRes.data].sort((a, b) => b.id - a.id).slice(0, 5);
    }
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
