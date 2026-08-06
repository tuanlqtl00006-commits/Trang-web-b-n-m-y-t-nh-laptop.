<template>
  <div v-if="order">
    <router-link to="/admin/orders" class="btn btn-sm btn-outline-secondary mb-3">
      <i class="bi bi-arrow-left"></i> Quay lại
    </router-link>
    <h4 class="fw-bold mb-4">Chi tiết đơn hàng #{{ order.id }}</h4>

    <div class="row g-4">
      <div class="col-md-7">
        <div class="card p-3">
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
          <h6 class="fw-bold">Thông tin khách hàng</h6>
          <p class="mb-1"><strong>Người nhận:</strong> {{ order.customerName }}</p>
          <p class="mb-1"><strong>Điện thoại:</strong> {{ order.phone }}</p>
          <p class="mb-1"><strong>Địa chỉ:</strong> {{ order.address }}</p>
          <p class="mb-1"><strong>Thanh toán:</strong> {{ order.paymentMethod }}</p>

          <div class="mb-2">
            <label class="form-label"><strong>Trạng thái</strong></label>
            <select v-model="order.status" class="form-select" @change="updateStatus">
              <option value="PENDING">Chờ xác nhận</option>
              <option value="CONFIRMED">Đã xác nhận</option>
              <option value="SHIPPING">Đang giao</option>
              <option value="COMPLETED">Hoàn thành</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
          </div>

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
import { getOrderById, getOrderItemsByOrder, updateOrder } from "@/api/order";

export default {
  name: "AdminOrderDetail",
  data() {
    return {
      order: null,
      items: [],
    };
  },
  async mounted() {
    const id = this.$route.params.id;
    const [oRes, iRes] = await Promise.all([getOrderById(id), getOrderItemsByOrder(id)]);
    this.order = oRes.data;
    this.items = iRes.data;
  },
  methods: {
    async updateStatus() {
      try {
        await updateOrder(this.order.id, this.order);
      } catch (e) {
        alert("Cập nhật trạng thái thất bại!");
      }
    },
    formatPrice(price) {
      if (!price) return "0";
      return price.toLocaleString("vi-VN");
    },
  },
};
</script>
