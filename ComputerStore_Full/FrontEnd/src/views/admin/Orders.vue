<template>
  <div>
    <h4 class="fw-bold mb-4">Quản lý đơn hàng</h4>

    <!-- Thanh tìm kiếm + lọc trạng thái -->
    <div class="d-flex gap-2 mb-3 flex-wrap">
      <div class="input-group" style="max-width: 320px">
        <span class="input-group-text bg-white"><i class="bi bi-search"></i></span>
        <input
          v-model="searchQuery"
          type="text"
          class="form-control"
          placeholder="Tìm theo tên, SĐT, mã đơn..."
          @input="page = 1"
        />
        <button v-if="searchQuery" class="btn btn-outline-secondary" @click="searchQuery = ''">
          <i class="bi bi-x"></i>
        </button>
      </div>
      <select v-model="filterStatus" class="form-select w-auto" @change="page = 1">
        <option value="">Tất cả trạng thái</option>
        <option value="PENDING">Chờ xác nhận</option>
        <option value="CONFIRMED">Đã xác nhận</option>
        <option value="SHIPPING">Đang giao</option>
        <option value="COMPLETED">Hoàn thành</option>
        <option value="CANCELLED">Đã hủy</option>
      </select>
      <span v-if="searchQuery || filterStatus" class="align-self-center text-muted small">
        Tìm thấy {{ filteredOrders.length }} đơn
      </span>
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
              <!-- Chỉ admin mới được xóa đơn hàng -->
              <button
                v-if="isAdmin"
                class="btn btn-sm btn-outline-danger"
                title="Xóa đơn hàng (chỉ admin)"
                @click="deleteOrder(o)"
              >
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </td>
        </tr>
        <tr v-if="pagedOrders.length === 0">
          <td colspan="7" class="text-center text-muted py-4">
            <span v-if="searchQuery || filterStatus">Không tìm thấy đơn hàng phù hợp.</span>
            <span v-else>Không có đơn hàng nào.</span>
          </td>
        </tr>
      </tbody>
    </table>

    <AppPagination v-model="page" :total-items="filteredOrders.length" :per-page="perPage" />
  </div>
</template>

<script>
import { getOrders, updateOrderStatus, deleteOrder as apiDeleteOrder } from "@/api/order";
import { toast } from "@/utils/toast";
import AppPagination from "@/components/Pagination.vue";
import { getUser } from "@/utils/session";

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
      searchQuery: "",
      page: 1,
      perPage: 10,
    };
  },
  computed: {
    isAdmin() {
      return (getUser()?.role || "").toLowerCase() === "admin";
    },
    filteredOrders() {
      let result = this.orders;
      if (this.filterStatus) {
        result = result.filter((o) => o.status === this.filterStatus);
      }
      if (this.searchQuery.trim()) {
        const kw = this.searchQuery.trim().toLowerCase();
        result = result.filter(
          (o) =>
            String(o.id).includes(kw) ||
            (o.customerName || "").toLowerCase().includes(kw) ||
            (o.phone || "").includes(kw)
        );
      }
      return result;
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
    async deleteOrder(order) {
      if (!confirm(`Xóa vĩnh viễn đơn hàng #${order.id}? Hành động này không thể hoàn tác!`)) return;
      try {
        await apiDeleteOrder(order.id);
        this.orders = this.orders.filter((o) => o.id !== order.id);
        toast.success(`Đã xóa đơn hàng #${order.id}!`);
      } catch (e) {
        toast.error(e.response?.data?.message || "Không thể xóa đơn hàng!");
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
