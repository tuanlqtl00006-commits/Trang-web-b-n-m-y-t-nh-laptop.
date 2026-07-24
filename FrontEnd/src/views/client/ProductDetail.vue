<template>
  <div v-if="product">
    <nav aria-label="breadcrumb" class="mb-3">
      <ol class="breadcrumb small">
        <li class="breadcrumb-item"><router-link to="/">Trang chủ</router-link></li>
        <li class="breadcrumb-item active">{{ product.name }}</li>
      </ol>
    </nav>

    <div class="row g-4">
      <div class="col-md-5">
        <div class="border rounded-4 p-3 text-center bg-white">
          <img :src="product.image" :alt="product.name" class="img-fluid" style="max-height: 350px" />
        </div>
      </div>

      <div class="col-md-7">
        <h3 class="fw-bold">{{ product.name }}</h3>
        <p class="price-text fs-3">{{ formatPrice(product.price) }}₫</p>

        <table class="table table-sm table-borderless">
          <tbody>
            <tr>
              <td class="text-muted" style="width: 140px">CPU</td>
              <td>{{ cpuName || "—" }}</td>
            </tr>
            <tr>
              <td class="text-muted">RAM</td>
              <td>{{ ramName || "—" }}</td>
            </tr>
            <tr>
              <td class="text-muted">Ổ cứng (SSD)</td>
              <td>{{ product.ssd || "—" }}</td>
            </tr>
            <tr>
              <td class="text-muted">Card đồ họa</td>
              <td>{{ product.gpu || "—" }}</td>
            </tr>
            <tr>
              <td class="text-muted">Màn hình</td>
              <td>{{ product.screen || "—" }}</td>
            </tr>
            <tr>
              <td class="text-muted">Hệ điều hành</td>
              <td>{{ product.os || "—" }}</td>
            </tr>
            <tr>
              <td class="text-muted">Thương hiệu</td>
              <td>{{ brandName || "—" }}</td>
            </tr>
            <tr>
              <td class="text-muted">Tình trạng</td>
              <td>
                <span v-if="product.stock > 0" class="badge bg-success">
                  Còn hàng ({{ product.stock }})
                </span>
                <span v-else class="badge bg-danger">Hết hàng</span>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="d-flex align-items-center gap-2 mb-3">
          <label class="mb-0">Số lượng:</label>
          <input
            type="number"
            v-model.number="quantity"
            min="1"
            :max="product.stock"
            class="form-control"
            style="width: 90px"
          />
        </div>

        <div class="d-flex gap-2">
          <button
            class="btn btn-outline-dark"
            :disabled="product.stock <= 0"
            @click="addToCart"
          >
            <i class="bi bi-cart-plus"></i> Thêm vào giỏ
          </button>
          <button
            class="btn btn-dark"
            :disabled="product.stock <= 0"
            @click="buyNow"
          >
            Mua ngay
          </button>
        </div>
      </div>
    </div>

    <div class="mt-4">
      <h5 class="fw-bold">Mô tả sản phẩm</h5>
      <p style="white-space: pre-line">{{ product.description }}</p>
    </div>
  </div>
  <div v-else class="text-center py-5">
    <div class="spinner-border" role="status"></div>
  </div>
</template>

<script>
import { getProductById } from "@/api/product";
import { getCpuById } from "@/api/cpu";
import { getRamById } from "@/api/ram";
import { getBrandById } from "@/api/brand";

export default {
  name: "ProductDetail",
  data() {
    return {
      product: null,
      quantity: 1,
      cpuName: "",
      ramName: "",
      brandName: "",
    };
  },
  watch: {
    "$route.params.id"() {
      this.loadProduct();
    },
  },
  mounted() {
    this.loadProduct();
  },
  methods: {
    async loadProduct() {
      this.product = null;
      const id = this.$route.params.id;
      try {
        const res = await getProductById(id);
        this.product = res.data;
        this.quantity = 1;

        if (this.product.cpuId) {
          getCpuById(this.product.cpuId).then((r) => (this.cpuName = r.data.cpuName));
        }
        if (this.product.ramId) {
          getRamById(this.product.ramId).then((r) => (this.ramName = r.data.ramName));
        }
        if (this.product.brandId) {
          getBrandById(this.product.brandId).then((r) => (this.brandName = r.data.brandName));
        }
      } catch (e) {
        console.error("Lỗi tải chi tiết sản phẩm:", e);
      }
    },
    addToCart() {
      const cart = JSON.parse(localStorage.getItem("cart") || "[]");
      const existing = cart.find((i) => i.id === this.product.id);
      if (existing) {
        existing.quantity += this.quantity;
      } else {
        cart.push({
          id: this.product.id,
          name: this.product.name,
          price: this.product.price,
          image: this.product.image,
          stock: this.product.stock,
          quantity: this.quantity,
        });
      }
      localStorage.setItem("cart", JSON.stringify(cart));
      window.dispatchEvent(new Event("cart-updated"));
      alert("Đã thêm vào giỏ hàng!");
    },
    buyNow() {
      this.addToCart();
      this.$router.push("/cart");
    },
    formatPrice(price) {
      if (!price) return "0";
      return price.toLocaleString("vi-VN");
    },
  },
};
</script>
