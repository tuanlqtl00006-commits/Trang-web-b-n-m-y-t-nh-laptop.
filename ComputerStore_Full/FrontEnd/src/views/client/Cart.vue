<template>
  <div>
    <h4 class="fw-bold mb-4"><i class="bi bi-cart3 me-2"></i>Giỏ hàng của bạn</h4>

    <div v-if="cart.length === 0" class="text-center py-5">
      <p class="text-muted">Giỏ hàng của bạn đang trống.</p>
      <router-link to="/" class="btn btn-dark">Tiếp tục mua sắm</router-link>
    </div>

    <div v-else class="row g-4">
      <div class="col-md-8">
        <table class="table align-middle">
          <thead>
            <tr>
              <th>Sản phẩm</th>
              <th>Đơn giá</th>
              <th style="width: 130px">Số lượng</th>
              <th>Thành tiền</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in cart" :key="item.id">
              <td class="d-flex align-items-center gap-2">
                <img :src="item.image" :alt="item.name" width="50" height="50" style="object-fit: contain" />
                <span>{{ item.name }}</span>
              </td>
              <td>{{ formatPrice(item.price) }}₫</td>
              <td>
                <input
                  type="number"
                  min="1"
                  :max="item.stock"
                  v-model.number="item.quantity"
                  class="form-control form-control-sm"
                  @change="updateCart"
                />
              </td>
              <td class="price-text">{{ formatPrice(item.price * item.quantity) }}₫</td>
              <td>
                <button class="btn btn-sm btn-outline-danger" @click="removeItem(item.id)">
                  <i class="bi bi-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="col-md-4">
        <div class="card p-3">
          <h5 class="fw-bold">Tóm tắt đơn hàng</h5>
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
          <button class="btn btn-dark w-100 mt-3" @click="goCheckout">
            Tiến hành thanh toán
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Cart",
  data() {
    return {
      cart: [],
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
    this.loadCart();
  },
  methods: {
    loadCart() {
      this.cart = JSON.parse(localStorage.getItem("cart") || "[]");
    },
    updateCart() {
      this.cart = this.cart.map((i) => ({
        ...i,
        quantity: Math.max(1, Math.min(i.quantity, i.stock || i.quantity)),
      }));
      localStorage.setItem("cart", JSON.stringify(this.cart));
      window.dispatchEvent(new Event("cart-updated"));
    },
    removeItem(id) {
      this.cart = this.cart.filter((i) => i.id !== id);
      localStorage.setItem("cart", JSON.stringify(this.cart));
      window.dispatchEvent(new Event("cart-updated"));
    },
    goCheckout() {
      if (!localStorage.getItem("user")) {
        this.$router.push("/login");
        return;
      }
      this.$router.push("/checkout");
    },
    formatPrice(price) {
      if (!price) return "0";
      return price.toLocaleString("vi-VN");
    },
  },
};
</script>
