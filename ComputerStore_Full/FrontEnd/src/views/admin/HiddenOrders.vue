<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold mb-0">
        <i class="bi bi-eye-slash text-danger me-2"></i>Đơn hàng đã ẩn
      </h4>
      <router-link to="/admin/orders" class="btn btn-outline-dark btn-sm">
        <i class="bi bi-arrow-left me-1"></i> Quay lại danh sách đơn hàng
      </router-link>
    </div>

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
        Tìm thấy {{ filteredOrders.length }} đơn đã ẩn
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
          <th style="width: 220px">Thao tác</th>
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
              <router-link :to="`/admin/orders/${o.id}`" class="btn btn-sm btn-outline-dark">
                Chi tiết
              </router-link>
              <button
                class="btn btn-sm btn-success"
                title="Khôi phục đơn hàng về danh sách quản lý"
                @click="restoreOrder(o)"
              >
                <i class="bi bi-arrow-counterclockwise me-1"></i>Khôi phục
              </button>
            </div>
          </td>
        </tr>
        <tr v-if="pagedOrders.length === 0">
          <td colspan="7" class="text-center text-muted py-4">
            <span v-if="searchQuery || filterStatus">Không tìm thấy đơn hàng ẩn phù hợp.</span>
            <span v-else>Không có đơn hàng nào bị ẩn.</span>
          </td>
        </tr>
      </tbody>
    </table>

    <AppPagination v-model="page" :total-items="filteredOrders.length" :per-page="perPage" />
  </div>
</template>

<script>
import { getOrders, unhideOrder as apiUnhideOrder } from "@/api/order";
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
  name: "AdminHiddenOrders",
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
      try {
        const res = await getOrders({ showHidden: true });
        this.orders = res.data.sort((a, b) => b.id - a.id);
      } catch (e) {
        toast.error("Không thể tải danh sách đơn hàng đã ẩn!");
      }
    },
    async restoreOrder(order) {
      if (!confirm(`Khôi phục đơn hàng #${order.id} về danh sách quản lý?`)) return;
      try {
        await apiUnhideOrder(order.id);
        this.orders = this.orders.filter((o) => o.id !== order.id);
        toast.success(`Đã khôi phục đơn hàng #${order.id}!`);
      } catch (e) {
        toast.error(e.response?.data?.message || "Không thể khôi phục đơn hàng!");
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
