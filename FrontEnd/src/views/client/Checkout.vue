<template>
  <div>
    <h4 class="fw-bold mb-4"><i class="bi bi-bag-check me-2"></i>Thanh toán</h4>

    <div v-if="cart.length === 0" class="text-center py-5 text-muted">
      Giỏ hàng trống, không có gì để thanh toán.
    </div>

    <div v-else class="row g-4">
      <div class="col-md-7">
        <div class="card p-3">
          <h5 class="fw-bold mb-3">Thông tin giao hàng</h5>
          <div class="mb-3">
            <label class="form-label">Họ tên người nhận</label>
            <input v-model="form.customerName" type="text" class="form-control" required />
          </div>
          <div class="mb-3">
            <label class="form-label">Số điện thoại</label>
            <input v-model="form.phone" type="text" class="form-control" required />
          </div>
          <div class="mb-3">
            <label class="form-label">Địa chỉ nhận hàng</label>
            <textarea v-model="form.address" class="form-control" rows="2" required></textarea>
          </div>
          <div class="mb-3">
            <label class="form-label">Phương thức thanh toán</label>
            <select v-model="form.paymentMethod" class="form-select">
              <option value="COD">Thanh toán khi nhận hàng (COD)</option>
              <option value="BANK">Chuyển khoản ngân hàng</option>
            </select>
          </div>
        </div>
      </div>

      <div class="col-md-5">
        <div class="card p-3">
          <h5 class="fw-bold mb-3">Đơn hàng của bạn</h5>
          <div v-for="item in cart" :key="item.id" class="d-flex justify-content-between small mb-2">
            <span>{{ item.name }} x{{ item.quantity }}</span>
            <span>{{ formatPrice(item.price * item.quantity) }}₫</span>
          </div>
          <hr />
          <div class="d-flex justify-content-between">
            <span>Tạm tính</span>
            <span>{{ formatPrice(subtotal) }}₫</span>
          </div>
          <div class="d-flex justify-content-between">
            <span>VAT (10%)</span>
            <span>{{ formatPrice(vat) }}₫</span>
          </div>
          <hr />
          <div class="d-flex justify-content-between fw-bold fs-5">
            <span>Tổng cộng</span>
            <span class="price-text">{{ formatPrice(total) }}₫</span>
          </div>
          <button class="btn btn-dark w-100 mt-3" :disabled="submitting" @click="placeOrder">
            {{ submitting ? "Đang xử lý..." : "Đặt hàng" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { createOrder, createOrderItem } from "@/api/order";

export default {
  name: "Checkout",
  data() {
    return {
      cart: [],
      submitting: false,
      form: {
        customerName: "",
        phone: "",
        address: "",
        paymentMethod: "COD",
      },
    };
  },
  computed: {
    subtotal() {
      return this.cart.reduce((s, i) => s + i.price * i.quantity, 0);
    },
    vat() {
      return Math.round(this.subtotal * 0.1);
    },
    total() {
      return this.subtotal + this.vat;
    },
  },
  mounted() {
    this.cart = JSON.parse(localStorage.getItem("cart") || "[]");
    const user = JSON.parse(localStorage.getItem("user") || "null");
    if (user) {
      this.form.customerName = user.fullname || "";
      this.form.phone = user.phone || "";
    }
  },
  methods: {
    async placeOrder() {
      if (!this.form.customerName || !this.form.phone || !this.form.address) {
        alert("Vui lòng nhập đầy đủ thông tin giao hàng!");
        return;
      }
      const user = JSON.parse(localStorage.getItem("user") || "null");
      if (!user) {
        this.$router.push("/login");
        return;
      }

      this.submitting = true;
      try {
        const orderRes = await createOrder({
          user: { id: user.id },
          customerName: this.form.customerName,
          phone: this.form.phone,
          address: this.form.address,
          paymentMethod: this.form.paymentMethod,
          subtotal: this.subtotal,
          vat: this.vat,
          total: this.total,
          orderDate: new Date().toISOString(),
        });

        const orderId = orderRes.data.id;

        await Promise.all(
          this.cart.map((item) =>
            createOrderItem({
              order: { id: orderId },
              product: { id: item.id },
              productName: item.name,
              quantity: item.quantity,
              price: item.price,
              image: item.image,
            })
          )
        );

        localStorage.removeItem("cart");
        window.dispatchEvent(new Event("cart-updated"));
        this.$router.push(`/order-success/${orderId}`);
      } catch (e) {
        console.error("Lỗi đặt hàng:", e);
        alert("Đặt hàng thất bại, vui lòng thử lại!");
      } finally {
        this.submitting = false;
      }
    },
    formatPrice(price) {
      if (!price) return "0";
      return price.toLocaleString("vi-VN");
    },
  },
};
</script>
