<template>
  <div class="d-flex flex-column min-vh-100">
    <!-- HEADER COMPUTER STORE (HOÀNG HÀ PC STYLE) -->
    <header class="hoangha-header">
      <!-- Main Blue Header Bar -->
      <div class="main-header py-2 text-white">
        <div class="container d-flex align-items-center justify-content-between gap-3">
          <!-- Logo -->
          <router-link class="navbar-brand d-flex align-items-center text-white text-decoration-none fw-bold fs-4 logo-text" to="/">
            <div class="logo-diamond">
              <span>CS</span>
            </div>
            <div class="d-flex flex-column leading-none">
              <span class="fw-black tracking-wider text-uppercase text-logo-main">COMPUTER STORE</span>
              <span class="small opacity-80 fw-normal text-logo-sub">HI-END PC • SERVER • WORKSTATION</span>
            </div>
          </router-link>

          <!-- Search Bar Pill (Hoàng Hà PC Style) -->
          <div class="flex-grow-1 mx-lg-4 search-wrapper-hh">
            <form @submit.prevent="handleSearch" class="position-relative d-flex align-items-center">
              <input
                type="text"
                v-model="searchKeyword"
                @input="handleSearch"
                placeholder="Nhập từ khóa tìm kiếm..."
                class="form-control hh-search-input rounded-pill pe-5 ps-4 py-2"
              />
              <button type="submit" class="btn btn-primary rounded-circle position-absolute end-0 me-1 p-0 d-flex align-items-center justify-content-center hh-search-btn" style="width: 36px; height: 36px;">
                <i class="bi bi-search text-white fs-6"></i>
              </button>
            </form>

            <!-- Search Dropdown Results -->
            <div v-if="searchResults.length > 0 && searchKeyword" class="search-results shadow-lg rounded-3">
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
          <!-- Quick Header Action Icons (Hoàng Hà PC) -->
          <div class="d-none d-md-flex align-items-center gap-3 text-white hh-actions">
            <!-- Cart -->
            <router-link to="/cart" class="d-flex align-items-center gap-2 text-white text-decoration-none position-relative ms-2">
              <div class="position-relative">
                <i class="bi bi-cart3 fs-3 text-white"></i>
                <span v-if="cartCount > 0" class="position-absolute top-0 start-100 translate-middle badge rounded-circle bg-danger d-flex align-items-center justify-content-center" style="font-size: 0.65rem; width: 18px; height: 18px; padding: 0;">
                  {{ cartCount }}
                </span>
              </div>
              <div class="lh-sm text-start" style="font-size: 0.82rem;">
                <div class="fw-semibold">Giỏ</div>
                <div class="opacity-90">Hàng</div>
              </div>
            </router-link>

            <!-- User Auth Pill Button -->
            <div v-if="user" class="dropdown ms-2">
              <button class="btn btn-login-hh rounded-pill px-3 py-1 d-flex align-items-center gap-2 text-white border-0 dropdown-toggle" data-bs-toggle="dropdown">
                <i class="bi bi-person fs-4"></i>
                <div class="lh-sm text-start fw-bold" style="font-size: 0.75rem; line-height: 1.1; max-width: 80px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                  <div>{{ displayName.split(' ')[0] || 'Tài' }}</div>
                  <div>{{ displayName.split(' ').slice(1).join(' ') || 'khoản' }}</div>
                </div>
              </button>
              <ul class="dropdown-menu dropdown-menu-end shadow">
                <li><router-link class="dropdown-item" to="/profile"><i class="bi bi-person me-2"></i>Thông tin tài khoản</router-link></li>
                <li><router-link class="dropdown-item" to="/orders"><i class="bi bi-bag-check me-2"></i>Đơn hàng của tôi</router-link></li>
                <li v-if="user.role && user.role.toLowerCase() === 'admin'"><router-link class="dropdown-item" to="/admin"><i class="bi bi-speedometer2 me-2"></i>Trang quản trị</router-link></li>
                <li><hr class="dropdown-divider" /></li>
                <li><a class="dropdown-item text-danger" @click="logout" href="#"><i class="bi bi-box-arrow-right me-2"></i>Đăng xuất</a></li>
              </ul>
            </div>
            <div v-else class="d-flex align-items-center gap-2 ms-2">
              <router-link to="/login" class="btn btn-login-hh rounded-pill px-3 py-1 d-flex align-items-center gap-2 text-white border-0">
                <i class="bi bi-person fs-4"></i>
                <div class="lh-sm text-start fw-bold" style="font-size: 0.75rem; line-height: 1.1;">
                  <div>Đăng</div>
                  <div>Nhập</div>
                </div>
              </router-link>
              <router-link to="/register" class="btn btn-register-hh rounded-pill px-3 py-1 d-flex align-items-center gap-2 text-white border-0">
                <i class="bi bi-person-plus fs-4"></i>
                <div class="lh-sm text-start fw-bold" style="font-size: 0.75rem; line-height: 1.1;">
                  <div>Đăng</div>
                  <div>Ký</div>
                </div>
              </router-link>
            </div>
          </div>
        </div>
      </div>

      <!-- Sub Navigation Bar (Hoàng Hà PC White Ribbon Bar) -->
      <div class="sub-nav-bar bg-white border-bottom shadow-sm py-2">
        <div class="container d-flex align-items-center justify-content-start gap-4 flex-wrap">
          <!-- Danh Mục Sản Phẩm Dropdown -->
          <div class="dropdown">
            <button class="sub-nav-item d-flex align-items-center gap-2 dropdown-toggle border-0 bg-transparent text-dark fw-bold px-3 py-2" type="button" data-bs-toggle="dropdown" aria-expanded="false">
              Danh Mục Sản Phẩm
            </button>
            <ul class="dropdown-menu shadow border-0 mt-2">
              <li v-for="cat in allCategories" :key="cat.id">
                <router-link class="dropdown-item py-2 px-3 fw-semibold text-dark" :to="`/category/${cat.id}`">
                  {{ cat.name }}
                </router-link>
              </li>
              <li v-if="allCategories.length === 0" class="dropdown-item text-muted py-2 px-3">
                Đang tải danh mục...
              </li>
            </ul>
          </div>


        </div>
      </div>
    </header>

    <main class="container my-4 flex-grow-1">
      <router-view @update-cart="updateCartCount" @user-logged-in="loadUser" />
    </main>

    <footer class="site-footer bg-dark text-white pt-5 mt-auto">
      <div class="container">
        <div class="row gy-4">
          <div class="col-md-4">
            <h5 class="fw-bold mb-3"><i class="bi bi-cpu me-2"></i>ComputerStore</h5>
            <p class="small text-secondary-light mb-0">
              Chuyên máy tính, laptop, linh kiện chính hãng. Uy tín – Chất lượng – Giá tốt.
            </p>
          </div>
          <div class="col-md-4">
            <h6 class="fw-bold mb-3">Danh mục</h6>
            <ul class="list-unstyled footer-links">
              <li v-for="cat in allCategories.slice(0, 5)" :key="cat.id" class="mb-2">
                <router-link :to="`/category/${cat.id}`">
                  {{ cat.name }}
                </router-link>
              </li>
            </ul>
          </div>
          <div class="col-md-4">
            <h6 class="fw-bold mb-3">Liên hệ</h6>
            <p class="small mb-2"><i class="bi bi-geo-alt me-2"></i>Hà Nội</p>
            <p class="small mb-2"><i class="bi bi-telephone me-2"></i>0123 456 789</p>
            <p class="small mb-0"><i class="bi bi-envelope me-2"></i>computerstore@gmail.com</p>
          </div>
        </div>
        <hr class="border-secondary mt-4" />
        <div class="text-center small text-secondary-light pb-4">
          © 2026 ComputerStore. All rights reserved.
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { getActiveProducts } from "@/api/product";
import { getCategories } from "@/api/category";
import { getUser, getCart, clearSession } from "@/utils/session";

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
      this.user = getUser();
    },
    loadCartCount() {
      const cart = getCart();
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
      clearSession();
      this.user = null;
      window.dispatchEvent(new Event("cart-updated"));
      this.$router.push("/login");
    },
  },
};
</script>

<style scoped>
/* ---------- COMPUTER STORE HEADER STYLES ---------- */
.hoangha-header {
  position: sticky;
  top: 0;
  z-index: 1030;
}
.main-header {
  background: linear-gradient(90deg, #0056f9 0%, #0066ff 40%, #0056f9 100%);
  box-shadow: 0 4px 12px rgba(0, 86, 249, 0.2);
}

/* Diamond Logo */
.logo-diamond {
  width: 40px;
  height: 40px;
  border: 2px solid #ffffff;
  transform: rotate(45deg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  margin-left: 8px;
  flex-shrink: 0;
  transition: transform 0.3s ease;
}
.logo-diamond:hover {
  transform: rotate(225deg);
}
.logo-diamond span {
  transform: rotate(-45deg);
  color: #ffffff;
  font-weight: 900;
  font-size: 1.1rem;
  font-family: "Arial Black", system-ui, sans-serif;
  letter-spacing: -1px;
}
.text-logo-main {
  font-size: 1.3rem;
  font-weight: 900;
  line-height: 1.1;
  letter-spacing: 0.05em;
}
.text-logo-sub {
  font-size: 0.65rem;
  letter-spacing: 0.06em;
}

/* Search bar styling */
.search-wrapper-hh {
  position: relative;
  max-width: 550px;
}
.hh-search-input {
  border: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 0.9rem;
  height: 42px;
  padding-left: 20px;
  padding-right: 52px;
  color: #333;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.08);
  transition: all 0.25s ease;
}
.hh-search-input:focus {
  border-color: #ffffff;
  box-shadow: 0 0 0 4px rgba(255, 255, 255, 0.25);
  outline: none;
}
.hh-search-btn {
  background-color: #0056f9;
  border: none;
  width: 34px;
  height: 34px;
  right: 4px;
  top: 4px;
  transition: background-color 0.2s ease, transform 0.1s ease;
}
.hh-search-btn:hover {
  background-color: #0042c6;
}
.hh-search-btn:active {
  transform: scale(0.95);
}

/* Quick icons styling */
.hh-actions {
  font-size: 0.85rem;
}
.btn-login-hh {
  background-color: #0042c6 !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  transition: background-color 0.2s ease, transform 0.1s ease;
}
.btn-login-hh:hover {
  background-color: #003399 !important;
}
.btn-login-hh:active {
  transform: scale(0.96);
}
.btn-register-hh {
  background-color: rgba(255, 255, 255, 0.15) !important;
  border: 1px solid rgba(255, 255, 255, 0.35) !important;
  transition: background-color 0.2s ease, transform 0.1s ease;
}
.btn-register-hh:hover {
  background-color: rgba(255, 255, 255, 0.28) !important;
}
.btn-register-hh:active {
  transform: scale(0.96);
}

/* Sub-nav ribbon bar */
.sub-nav-bar {
  background-color: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}
.sub-nav-item {
  font-size: 0.88rem;
  font-weight: 600;
  color: #2d3748 !important;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.2s ease;
}
.sub-nav-item:hover {
  color: #0056f9 !important;
  background-color: #f0f7ff;
}
.sub-nav-item i {
  color: #0066ff !important;
  font-size: 1.15rem;
  transition: transform 0.2s ease;
}
.sub-nav-item:hover i {
  transform: scale(1.15);
}
.cursor-pointer {
  cursor: pointer;
}

/* Search results dropdown */
.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  max-height: 400px;
  overflow-y: auto;
  margin-top: 5px;
}
.search-result-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  cursor: pointer;
  border-bottom: 1px solid #edf2f7;
  color: #2d3748;
  transition: background-color 0.15s ease;
}
.search-result-item:hover {
  background-color: #f7fafc;
}
.result-img {
  width: 40px;
  height: 40px;
  object-fit: contain;
}
.result-info {
  display: flex;
  flex-direction: column;
}
.result-name {
  font-size: 0.88rem;
  font-weight: 600;
  color: #1a202c;
  line-height: 1.2;
}
.result-price {
  font-size: 0.8rem;
  color: #d70018;
  font-weight: 700;
  margin-top: 2px;
}

/* ---------- Footer ---------- */
.site-footer h5,
.site-footer h6 {
  color: #ffffff;
}
.text-secondary-light {
  color: rgba(255, 255, 255, 0.65) !important;
}
.footer-links a {
  color: rgba(255, 255, 255, 0.75);
  text-decoration: none;
  font-size: 0.875rem;
  transition: color 0.15s ease;
}
.footer-links a:hover {
  color: #ffffff;
  text-decoration: underline;
}
</style>
