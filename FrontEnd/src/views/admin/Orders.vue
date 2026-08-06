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
          <th style="width: 100px"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="o in filteredOrders" :key="o.id">
          <td>#{{ o.id }}</td>
          <td>{{ o.customerName }}</td>
          <td>{{ o.phone }}</td>
          <td class="price-text">{{ formatPrice(o.total) }}₫</td>
          <td>{{ formatDate(o.orderDate) }}</td>
          <td>
            <select
              class="form-select form-select-sm"
              :value="o.status"
              @change="changeStatus(o, $event.target.value)"
            >
              <option value="PENDING">Chờ xác nhận</option>
              <option value="CONFIRMED">Đã xác nhận</option>
              <option value="SHIPPING">Đang giao</option>
              <option value="COMPLETED">Hoàn thành</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
          </td>
          <td>
            <router-link :to="`/admin/orders/${o.id}`" class="btn btn-sm btn-outline-dark">
              Chi tiết
            </router-link>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { getOrders, updateOrder } from "@/api/order";

export default {
  name: "AdminOrders",
  data() {
    return {
      orders: [],
      filterStatus: "",
    };
  },
  computed: {
    filteredOrders() {
      if (!this.filterStatus) return this.orders;
      return this.orders.filter((o) => o.status === this.filterStatus);
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
    async changeStatus(order, newStatus) {
      try {
        await updateOrder(order.id, { ...order, status: newStatus });
        order.status = newStatus;
      } catch (e) {
        alert("Cập nhật trạng thái thất bại!");
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
  },
};
</script>
