<template>
  <div>
    <h4 class="fw-bold mb-4">Quản lý đơn hàng</h4>

    <div class="mb-3">
      <select v-model="filterStatus" class="form-select w-auto">
        <option value="">Tất cả trạng thái</option>
        <option value="PENDING">Chờ xác nhận</option>
        <option value="CONFIRMED">Đã xác nhận</option>
        <option value="SHIPPING">Đang giao</option>
        <option value="COMPLETED">Hoàn thành</option>
        <option value="CANCELLED">Đã hủy</option>
      </select>
    </div>

    <table class="table align-middle bg-white">
      <thead>
        <tr>
          <th>Mã đơn</th>
          <th>Khách hàng</th>
          <th>Điện thoại</th>
          <th>Tổng tiền</th>
          <th>Ngày đặt</th>
          <th>Trạng thái</th>
          <th style="width: 280px">Xử lý</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="o in pagedOrders" :key="o.id">
          <td>#{{ o.id }}</td>
          <td>{{ o.customerName }}</td>
          <td>{{ o.phone }}</td>
          <td class="price-text">{{ formatPrice(o.total) }}₫</td>
          <td>{{ formatDate(o.orderDate) }}</td>
          <td>
            <span class="badge" :class="statusBadgeClass(o.status)">{{ statusLabel(o.status) }}</span>
          </td>
          <td>
            <div class="d-flex flex-wrap gap-1">
              <button
                v-if="o.status === 'PENDING'"
                class="btn btn-sm btn-primary"
                @click="setStatus(o, 'CONFIRMED')"
              >
                <i class="bi bi-check2"></i> Xác nhận
              </button>
              <button
                v-if="o.status === 'CONFIRMED'"
                class="btn btn-sm btn-primary"
                @click="setStatus(o, 'SHIPPING')"
              >
                <i class="bi bi-truck"></i> Giao hàng
              </button>
              <button
                v-if="o.status === 'PENDING' || o.status === 'CONFIRMED'"
                class="btn btn-sm btn-outline-danger"
                @click="cancelOrder(o)"
              >
                <i class="bi bi-x-circle"></i> Hủy
              </button>
              <span v-if="o.status === 'SHIPPING'" class="text-muted small align-self-center">
                Đang chờ khách nhận hàng...
              </span>
              <router-link :to="`/admin/orders/${o.id}`" class="btn btn-sm btn-outline-dark">
                Chi tiết
              </router-link>
            </div>
          </td>
        </tr>
        <tr v-if="pagedOrders.length === 0">
          <td colspan="7" class="text-center text-muted py-4">Không có đơn hàng nào.</td>
        </tr>
      </tbody>
    </table>

    <AppPagination v-model="page" :total-items="filteredOrders.length" :per-page="perPage" />
  </div>
</template>

<script>
import { getOrders, updateOrderStatus } from "@/api/order";
import { toast } from "@/utils/toast";
import AppPagination from "@/components/Pagination.vue";

const STATUS_LABELS = {
  PENDING: "Chờ xác nhận",
  CONFIRMED: "Đã xác nhận",
  SHIPPING: "Đang giao",
  COMPLETED: "Hoàn thành",
  CANCELLED: "Đã hủy",
};

const STATUS_CLASSES = {
  PENDING: "badge-status-pending",
  CONFIRMED: "badge-status-confirmed",
  SHIPPING: "badge-status-shipping",
  COMPLETED: "badge-status-completed",
  CANCELLED: "badge-status-cancelled",
};

export default {
  name: "AdminOrders",
  components: { AppPagination },
  data() {
    return {
      orders: [],
      filterStatus: "",
      page: 1,
      perPage: 10,
    };
  },
  computed: {
    filteredOrders() {
      if (!this.filterStatus) return this.orders;
      return this.orders.filter((o) => o.status === this.filterStatus);
    },
    pagedOrders() {
      const start = (this.page - 1) * this.perPage;
      return this.filteredOrders.slice(start, start + this.perPage);
    },
  },
  mounted() {
    this.loadOrders();
  },
  methods: {
    async loadOrders() {
      const res = await getOrders();
      this.orders = res.data.sort((a, b) => b.id - a.id);
    },
    async setStatus(order, newStatus) {
      try {
        const res = await updateOrderStatus(order.id, newStatus);
        order.status = res.data.status;
        toast.success(
          newStatus === "CONFIRMED"
            ? "Đã xác nhận đơn hàng, chuyển sang trạng thái giao hàng khi sẵn sàng."
            : "Đã chuyển đơn hàng sang trạng thái đang giao!"
        );
      } catch (e) {
        toast.error(e.response?.data?.message || "Cập nhật trạng thái thất bại!");
      }
    },
    async cancelOrder(order) {
      const reason = prompt("Nhập lý do hủy đơn hàng:", "Khách hàng yêu cầu hủy");
      if (reason === null) return;
      try {
        const res = await updateOrderStatus(order.id, "CANCELLED", reason);
        order.status = res.data.status;
        toast.success("Đã hủy đơn hàng!");
      } catch (e) {
        toast.error(e.response?.data?.message || "Không thể hủy đơn hàng!");
      }
    },
    statusLabel(status) {
      return STATUS_LABELS[status] || status;
    },
    statusBadgeClass(status) {
      return STATUS_CLASSES[status] || "bg-secondary";
    },
    formatPrice(price) {
      if (!price) return "0";
      return price.toLocaleString("vi-VN");
    },
    formatDate(date) {
      if (!date) return "—";
      return new Date(date).toLocaleString("vi-VN");
    },
  },
};
</script>
