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
          <th style="width: 320px"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="o in pagedOrders" :key="o.id">
          <td>#{{ o.id }}</td>
          <td>{{ formatDate(o.orderDate) }}</td>
          <td class="price-text">{{ formatPrice(o.total) }}₫</td>
          <td>
            <span class="badge" :class="statusClass(o.status)">
              {{ statusLabel(o.status) }}
            </span>
          </td>
          <td>
            <div class="d-flex flex-wrap gap-1">
              <button
                v-if="o.status === 'SHIPPING'"
                class="btn btn-sm btn-success"
                @click="receiveOrder(o)"
              >
                <i class="bi bi-box-seam"></i> Đã nhận hàng & thanh toán
              </button>
              <button
                v-if="o.status === 'PENDING'"
                class="btn btn-sm btn-outline-danger"
                @click="cancelOrder(o)"
              >
                <i class="bi bi-x-circle"></i> Hủy đơn
              </button>
              <router-link :to="`/order-detail/${o.id}`" class="btn btn-sm btn-outline-dark">
                Chi tiết
              </router-link>
            </div>
          </td>
        </tr>
      </tbody>
    </table>

    <AppPagination v-if="!loading" v-model="page" :total-items="orders.length" :per-page="perPage" />
  </div>
</template>

<script>
import { getOrdersByUser, updateOrderStatus } from "@/api/order";
import { toast } from "@/utils/toast";
import AppPagination from "@/components/Pagination.vue";

export default {
  name: "ClientOrders",
  components: { AppPagination },
  data() {
    return {
      orders: [],
      loading: true,
      page: 1,
      perPage: 8,
    };
  },
  computed: {
    pagedOrders() {
      const start = (this.page - 1) * this.perPage;
      return this.orders.slice(start, start + this.perPage);
    },
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
    async receiveOrder(order) {
      if (!confirm("Xác nhận bạn đã nhận hàng và thanh toán?")) return;
      try {
        const res = await updateOrderStatus(order.id, "COMPLETED");
        order.status = res.data.status;
        toast.success("Cảm ơn bạn! Đơn hàng đã hoàn thành.");
      } catch (e) {
        toast.error(e.response?.data?.message || "Không thể cập nhật đơn hàng!");
      }
    },
    async cancelOrder(order) {
      const reason = prompt("Vui lòng cho biết lý do hủy đơn:", "Tôi không còn muốn mua nữa");
      if (reason === null) return;
      try {
        const res = await updateOrderStatus(order.id, "CANCELLED", reason);
        order.status = res.data.status;
        toast.success("Đã hủy đơn hàng!");
      } catch (e) {
        toast.error(e.response?.data?.message || "Không thể hủy đơn hàng!");
      }
    },
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
