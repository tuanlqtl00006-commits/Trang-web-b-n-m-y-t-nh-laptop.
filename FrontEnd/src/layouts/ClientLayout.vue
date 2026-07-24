<template>
  <div class="d-flex flex-column min-vh-100">
    <!-- HEADER -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark border-bottom">
      <div class="container">
        <router-link class="navbar-brand fw-bold" to="/">
          <i class="bi bi-cpu me-1"></i> ComputerStore
        </router-link>

        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#clientMenu"
        >
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="clientMenu">
          <ul class="navbar-nav mx-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <router-link class="nav-link" to="/">Trang chủ</router-link>
            </li>
            <li
              v-for="category in allCategories"
              :key="category.id"
              class="nav-item"
            >
              <router-link class="nav-link" :to="`/category/${category.id}`">
                {{ category.name }}
              </router-link>
            </li>
          </ul>

          <div class="d-flex align-items-center gap-3">
            <div class="search-wrapper">
              <form @submit.prevent="handleSearch" class="d-flex">
                <input
                  type="text"
                  v-model="searchKeyword"
                  @input="handleSearch"
                  placeholder="Tìm sản phẩm..."
                  class="form-control form-control-sm search-input"
                />
              </form>
              <div
                v-if="searchResults.length > 0 && searchKeyword"
                class="search-results"
              >
                <div
                  v-for="result in searchResults"
                  :key="result.id"
                  class="search-result-item"
                  @click="goToProduct(result.id)"
                >
                  <img :src="result.image" :alt="result.name" class="result-img" />
                  <div class="result-info">
                    <div class="result-name">{{ result.name }}</div>
                    <div class="result-price">{{ formatPrice(result.price) }}₫</div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="user" class="dropdown">
              <button
                class="btn btn-link text-white text-decoration-none dropdown-toggle"
                data-bs-toggle="dropdown"
              >
                <i class="bi bi-person-circle me-1"></i>{{ displayName }}
              </button>
              <ul class="dropdown-menu dropdown-menu-end">
                <li>
                  <router-link class="dropdown-item" to="/profile">
                    <i class="bi bi-person me-2"></i>Thông tin tài khoản
                  </router-link>
                </li>
                <li>
                  <router-link class="dropdown-item" to="/orders">
                    <i class="bi bi-bag-check me-2"></i>Đơn hàng của tôi
                  </router-link>
                </li>
                <li v-if="user.role && user.role.toLowerCase() === 'admin'">
                  <router-link class="dropdown-item" to="/admin">
                    <i class="bi bi-speedometer2 me-2"></i>Trang quản trị
                  </router-link>
                </li>
                <li><hr class="dropdown-divider" /></li>
                <li>
                  <a class="dropdown-item text-danger" @click="logout" href="#">
                    <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
                  </a>
                </li>
              </ul>
            </div>
            <div v-else>
              <router-link to="/login" class="btn btn-outline-light btn-sm">
                <i class="bi bi-person"></i> Đăng nhập
              </router-link>
            </div>

            <router-link to="/cart" class="btn btn-outline-light position-relative">
              <i class="bi bi-cart3"></i> Giỏ hàng
              <span
                v-if="cartCount > 0"
                class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
              >
                {{ cartCount }}
              </span>
            </router-link>
          </div>
        </div>
      </div>
    </nav>

    <main class="container my-4 flex-grow-1">
      <router-view @update-cart="updateCartCount" @user-logged-in="loadUser" />
    </main>

    <footer class="bg-dark text-white pt-4 mt-auto">
      <div class="container">
        <div class="row">
          <div class="col-md-4 mb-3">
            <h5 class="fw-bold"><i class="bi bi-cpu me-1"></i>ComputerStore</h5>
            <p class="small">
              Chuyên máy tính, laptop, linh kiện chính hãng. Uy tín – Chất lượng – Giá tốt.
            </p>
          </div>
          <div class="col-md-4 mb-3">
            <h6 class="fw-bold">Danh mục</h6>
            <ul class="list-unstyled">
              <li v-for="cat in allCategories.slice(0, 5)" :key="cat.id">
                <router-link
                  :to="`/category/${cat.id}`"
                  class="text-white text-decoration-none small"
                >
                  {{ cat.name }}
                </router-link>
              </li>
            </ul>
          </div>
          <div class="col-md-4 mb-3">
            <h6 class="fw-bold">Liên hệ</h6>
            <p class="small mb-1">📍 Hà Nội</p>
            <p class="small mb-1">📞 0123 456 789</p>
            <p class="small">✉ computerstore@gmail.com</p>
          </div>
        </div>
        <hr class="border-secondary" />
        <div class="text-center small pb-3">© 2026 ComputerStore. All rights reserved.</div>
      </div>
    </footer>
  </div>
</template>

<script>
import { getActiveProducts } from "@/api/product";
import { getCategories } from "@/api/category";

export default {
  name: "ClientLayout",
  data() {
    return {
      user: null,
      cartCount: 0,
      searchKeyword: "",
      searchResults: [],
      allProducts: [],
      allCategories: [],
    };
  },
  computed: {
    displayName() {
      if (!this.user) return "";
      return this.user.fullname || this.user.email || "Khách hàng";
    },
  },
  mounted() {
    this.loadUser();
    this.loadCartCount();
    this.loadAllProducts();
    this.loadCategories();
    window.addEventListener("cart-updated", this.loadCartCount);
    window.addEventListener("user-logged-in", this.loadUser);
  },
  beforeUnmount() {
    window.removeEventListener("cart-updated", this.loadCartCount);
    window.removeEventListener("user-logged-in", this.loadUser);
  },
  methods: {
    loadUser() {
      const userData = localStorage.getItem("user");
      this.user = userData ? JSON.parse(userData) : null;
    },
    loadCartCount() {
      const cart = JSON.parse(localStorage.getItem("cart") || "[]");
      this.cartCount = cart.reduce((t, i) => t + (i.quantity || 1), 0);
    },
    updateCartCount() {
      this.loadCartCount();
    },
    async loadAllProducts() {
      try {
        const res = await getActiveProducts();
        this.allProducts = res.data;
      } catch (e) {
        console.error("Lỗi tải sản phẩm:", e);
      }
    },
    async loadCategories() {
      try {
        const res = await getCategories();
        this.allCategories = res.data;
      } catch (e) {
        console.error("Lỗi tải danh mục:", e);
      }
    },
    handleSearch() {
      if (!this.searchKeyword.trim()) {
        this.searchResults = [];
        return;
      }
      const keyword = this.searchKeyword.toLowerCase().trim();
      this.searchResults = this.allProducts
        .filter((p) => p.name.toLowerCase().includes(keyword))
        .slice(0, 5);
    },
    goToProduct(id) {
      this.searchKeyword = "";
      this.searchResults = [];
      this.$router.push(`/product/${id}`);
    },
    formatPrice(price) {
      if (!price) return "0";
      return price.toLocaleString("vi-VN");
    },
    logout() {
      localStorage.removeItem("user");
      localStorage.removeItem("token");
      this.user = null;
      window.dispatchEvent(new Event("cart-updated"));
      this.$router.push("/login");
    },
  },
};
</script>

<style scoped>
.search-wrapper {
  position: relative;
  width: 220px;
}
.search-input {
  border-radius: 20px;
}
.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  max-height: 400px;
  overflow-y: auto;
  margin-top: 5px;
}
.search-result-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
  color: #333;
}
.search-result-item:hover {
  background-color: #f8f9fa;
}
.result-img {
  width: 40px;
  height: 40px;
  object-fit: contain;
}
.result-name {
  font-size: 0.85rem;
  font-weight: 500;
}
.result-price {
  font-size: 0.75rem;
  color: #d70018;
  font-weight: bold;
}
@media (max-width: 992px) {
  .search-wrapper {
    width: 100%;
    margin: 10px 0;
  }
}
</style>
