<template>
  <div v-if="order">
    <h4 class="fw-bold mb-4">Chi tiết đơn hàng #{{ order.id }}</h4>

    <div class="row g-4">
      <div class="col-md-7">
        <div class="card p-3 mb-3">
          <h6 class="fw-bold">Sản phẩm</h6>
          <table class="table align-middle mb-0">
            <tbody>
              <tr v-for="item in items" :key="item.id">
                <td style="width: 60px">
                  <img :src="item.image" width="50" height="50" style="object-fit: contain" />
                </td>
                <td>{{ item.productName }}</td>
                <td>x{{ item.quantity }}</td>
                <td class="text-end">{{ formatPrice(item.price * item.quantity) }}₫</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="col-md-5">
        <div class="card p-3">
          <h6 class="fw-bold">Thông tin giao hàng</h6>
          <p class="mb-1"><strong>Người nhận:</strong> {{ order.customerName }}</p>
          <p class="mb-1"><strong>Điện thoại:</strong> {{ order.phone }}</p>
          <p class="mb-1"><strong>Địa chỉ:</strong> {{ order.address }}</p>
          <p class="mb-1"><strong>Thanh toán:</strong> {{ order.paymentMethod }}</p>
          <p class="mb-1">
            <strong>Trạng thái:</strong>
            <span class="badge" :class="statusClass(order.status)">{{ statusLabel(order.status) }}</span>
          </p>
          <hr />
          <div class="d-flex justify-content-between">
            <span>Tạm tính</span><span>{{ formatPrice(order.subtotal) }}₫</span>
          </div>
          <div class="d-flex justify-content-between">
            <span>VAT</span><span>{{ formatPrice(order.vat) }}₫</span>
          </div>
          <div class="d-flex justify-content-between fw-bold fs-5">
            <span>Tổng cộng</span><span class="price-text">{{ formatPrice(order.total) }}₫</span>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="text-center py-5">
    <div class="spinner-border" role="status"></div>
  </div>
</template>

<script>
import { getOrderById, getOrderItemsByOrder } from "@/api/order";

export default {
  name: "OrderDetail",
  data() {
    return {
      order: null,
      items: [],
    };
  },
  async mounted() {
    const id = this.$route.params.id;
    try {
      const [oRes, iRes] = await Promise.all([getOrderById(id), getOrderItemsByOrder(id)]);
      this.order = oRes.data;
      this.items = iRes.data;
    } catch (e) {
      console.error("Lỗi tải chi tiết đơn hàng:", e);
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
