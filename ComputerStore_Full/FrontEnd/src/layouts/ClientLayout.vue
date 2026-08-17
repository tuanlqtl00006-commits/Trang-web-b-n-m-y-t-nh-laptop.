<template>
  <div class="d-flex flex-column min-vh-100 client-shell">
    <header class="store-header">
      <div class="topline d-none d-lg-block">
        <div class="container d-flex justify-content-between align-items-center py-1">
          <span><i class="bi bi-shield-check me-1"></i> Laptop chính hãng • Bảo hành minh bạch</span>
          <div class="d-flex gap-3">
            <span><i class="bi bi-telephone me-1"></i> Hotline: 0123 456 789</span>
            <span><i class="bi bi-geo-alt me-1"></i> Hà Nội</span>
          </div>
        </div>
      </div>

      <div class="main-header text-white">
        <div class="container header-grid py-2 py-lg-3">
          <router-link class="brand-lockup text-white text-decoration-none" to="/">
            <div class="logo-mark"><i class="bi bi-laptop"></i></div>
            <div class="brand-copy d-none d-sm-flex flex-column">
              <span class="brand-name">COMPUTER STORE</span>
              <span class="brand-sub">LAPTOP • PC • WORKSTATION</span>
            </div>
          </router-link>

          <div class="search-wrapper">
            <form @submit.prevent="submitSearch" class="position-relative">
              <i class="bi bi-search search-leading"></i>
              <input
                ref="searchInput"
                v-model="searchKeyword"
                type="search"
                autocomplete="off"
                class="form-control store-search"
                placeholder="Tìm laptop theo tên sản phẩm..."
                @input="handleSearch"
                @focus="handleSearch"
                @keydown.esc="closeSearch"
              />
              <button v-if="searchKeyword" class="search-clear" type="button" aria-label="Xóa tìm kiếm" @click="clearSearch">
                <i class="bi bi-x-lg"></i>
              </button>
            </form>

            <div v-if="searchOpen && searchKeyword.trim()" class="search-results">
              <template v-if="searchResults.length">
                <div class="search-results-header">Gợi ý sản phẩm</div>
                <button
                  v-for="result in searchResults"
                  :key="result.id"
                  type="button"
                  class="search-result-item w-100 text-start"
                  @click="goToProduct(result.id)"
                >
                  <img :src="result.image" :alt="result.name" class="result-img" />
                  <div class="result-info flex-grow-1 overflow-hidden">
                    <div class="result-name text-truncate">{{ result.name }}</div>
                    <div class="d-flex align-items-center gap-2 mt-1">
                      <span class="result-price">{{ formatPrice(result.price) }}₫</span>
                      <span v-if="result.stock > 0" class="result-stock">Còn hàng</span>
                    </div>
                  </div>
                  <i class="bi bi-chevron-right text-muted"></i>
                </button>
              </template>
              <div v-else class="search-empty text-center p-4">
                <i class="bi bi-search fs-4 d-block mb-2"></i>
                Không tìm thấy sản phẩm phù hợp.
              </div>
            </div>
          </div>

          <div class="header-actions">
            <router-link to="/wishlist" class="header-action" title="Yêu thích">
              <div class="action-icon position-relative">
                <i class="bi bi-heart"></i>
                <span v-if="wishlistCount > 0" class="action-badge">{{ wishlistCount > 99 ? '99+' : wishlistCount }}</span>
              </div>
              <span class="d-none d-xl-block">Yêu thích</span>
            </router-link>

            <router-link to="/cart" class="header-action" title="Giỏ hàng">
              <div class="action-icon position-relative">
                <i class="bi bi-cart3"></i>
                <span v-if="cartCount > 0" class="action-badge">{{ cartCount > 99 ? '99+' : cartCount }}</span>
              </div>
              <span class="d-none d-xl-block">Giỏ hàng</span>
            </router-link>

            <div v-if="user" class="dropdown">
              <button class="header-action user-action dropdown-toggle border-0" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                <div class="avatar-circle">{{ userInitial }}</div>
                <span class="d-none d-xl-block user-name text-truncate">{{ displayName }}</span>
              </button>
              <ul class="dropdown-menu dropdown-menu-end shadow border-0 account-menu mt-2">
                <li class="px-3 py-2 border-bottom mb-1">
                  <div class="fw-bold text-truncate">{{ displayName }}</div>
                  <div class="small text-muted text-truncate">{{ user.email || roleLabel }}</div>
                </li>
                <li><router-link class="dropdown-item" to="/profile"><i class="bi bi-person me-2"></i>Thông tin tài khoản</router-link></li>
                <li><router-link class="dropdown-item" to="/orders"><i class="bi bi-bag-check me-2"></i>Đơn hàng của tôi</router-link></li>
                <li v-if="canAccessAdmin"><router-link class="dropdown-item" to="/admin"><i class="bi bi-speedometer2 me-2"></i>Trang quản trị</router-link></li>
                <li><hr class="dropdown-divider" /></li>
                <li><button class="dropdown-item text-danger" type="button" @click="logout"><i class="bi bi-box-arrow-right me-2"></i>Đăng xuất</button></li>
              </ul>
            </div>

            <router-link v-else to="/login" class="header-action login-action">
              <div class="action-icon"><i class="bi bi-person"></i></div>
              <span class="d-none d-xl-block">Đăng nhập</span>
            </router-link>
          </div>
        </div>
      </div>

      <nav class="sub-nav-bar bg-white border-bottom shadow-sm">
        <div class="container sub-nav-scroll py-2">
          <router-link to="/" class="sub-nav-item" exact-active-class="active"><i class="bi bi-house-door"></i> Trang chủ</router-link>

          <div class="dropdown flex-shrink-0">
            <button class="sub-nav-item dropdown-toggle border-0 bg-transparent" type="button" data-bs-toggle="dropdown" aria-expanded="false">
              <i class="bi bi-grid"></i> Danh mục sản phẩm
            </button>
            <ul class="dropdown-menu category-menu shadow border-0 mt-2">
              <li v-for="cat in allCategories" :key="cat.id">
                <router-link class="dropdown-item py-2" :to="`/category/${cat.id}`">
                  <i class="bi bi-laptop me-2 text-primary"></i>{{ cat.name }}
                </router-link>
              </li>
              <li v-if="allCategories.length === 0" class="dropdown-item text-muted py-2">Đang tải danh mục...</li>
            </ul>
          </div>

          <router-link to="/wishlist" class="sub-nav-item"><i class="bi bi-heart"></i> Yêu thích <span v-if="wishlistCount" class="nav-count">{{ wishlistCount }}</span></router-link>
          <router-link to="/cart" class="sub-nav-item"><i class="bi bi-cart3"></i> Giỏ hàng <span v-if="cartCount" class="nav-count">{{ cartCount }}</span></router-link>
          <router-link v-if="user" to="/orders" class="sub-nav-item"><i class="bi bi-receipt"></i> Đơn hàng</router-link>
          <span class="sub-nav-spacer"></span>
          <span class="sub-nav-message d-none d-lg-inline-flex"><i class="bi bi-lightning-charge-fill"></i> Ưu đãi mùa tựu trường đang diễn ra</span>
        </div>
      </nav>
    </header>

    <main class="container my-3 my-lg-4 flex-grow-1">
      <router-view @update-cart="updateCartCount" @user-logged-in="loadUser" />
    </main>

    <footer class="site-footer mt-auto">
      <div class="footer-newsletter border-bottom border-secondary border-opacity-25">
        <div class="container py-4 d-flex flex-wrap align-items-center justify-content-between gap-3">
          <div class="d-flex align-items-center gap-3">
            <div class="newsletter-icon"><i class="bi bi-chat-square-heart"></i></div>
            <div>
              <h5 class="mb-1 fw-bold text-white">Cần tư vấn chọn laptop?</h5>
              <p class="mb-0 small footer-muted">ComputerStore hỗ trợ chọn cấu hình phù hợp với học tập và công việc.</p>
            </div>
          </div>
          <router-link to="/" class="btn btn-light rounded-pill px-4 fw-bold">Xem sản phẩm <i class="bi bi-arrow-right ms-1"></i></router-link>
        </div>
      </div>

      <div class="container pt-5 pb-4">
        <div class="row gy-4">
          <div class="col-lg-4">
            <div class="d-flex align-items-center gap-2 mb-3">
              <div class="footer-logo"><i class="bi bi-laptop"></i></div>
              <div>
                <div class="fw-black text-white">COMPUTER STORE</div>
                <div class="footer-muted footer-brand-sub">LAPTOP • PC • WORKSTATION</div>
              </div>
            </div>
            <p class="small footer-muted mb-3">Cửa hàng máy tính hướng tới trải nghiệm mua sắm rõ ràng, nhanh chóng và thuận tiện.</p>
            <div class="d-flex gap-2">
              <span class="social-button"><i class="bi bi-facebook"></i></span>
              <span class="social-button"><i class="bi bi-youtube"></i></span>
              <span class="social-button"><i class="bi bi-messenger"></i></span>
            </div>
          </div>

          <div class="col-6 col-lg-2">
            <h6 class="fw-bold text-white mb-3">Danh mục</h6>
            <ul class="list-unstyled footer-links mb-0">
              <li v-for="cat in allCategories.slice(0, 5)" :key="cat.id" class="mb-2"><router-link :to="`/category/${cat.id}`">{{ cat.name }}</router-link></li>
            </ul>
          </div>

          <div class="col-6 col-lg-2">
            <h6 class="fw-bold text-white mb-3">Hỗ trợ</h6>
            <ul class="list-unstyled footer-links mb-0">
              <li class="mb-2"><router-link to="/cart">Giỏ hàng</router-link></li>
              <li class="mb-2"><router-link to="/wishlist">Yêu thích</router-link></li>
              <li class="mb-2"><router-link to="/orders">Tra cứu đơn hàng</router-link></li>
              <li class="mb-2"><router-link to="/profile">Tài khoản</router-link></li>
            </ul>
          </div>

          <div class="col-lg-4">
            <h6 class="fw-bold text-white mb-3">Thông tin liên hệ</h6>
            <div class="contact-row"><i class="bi bi-geo-alt"></i><span>Hà Nội, Việt Nam</span></div>
            <div class="contact-row"><i class="bi bi-telephone"></i><span>0123 456 789</span></div>
            <div class="contact-row"><i class="bi bi-envelope"></i><span>computerstore@gmail.com</span></div>
            <div class="contact-row"><i class="bi bi-clock"></i><span>08:00 - 21:00 mỗi ngày</span></div>
          </div>
        </div>

        <hr class="border-secondary border-opacity-25 mt-4" />
        <div class="d-flex flex-wrap justify-content-between gap-2 small footer-muted">
          <span>© 2026 ComputerStore. All rights reserved.</span>
          <span>Uy tín • Chất lượng • Giá tốt</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { getActiveProducts } from "@/api/product";
import { getCategories } from "@/api/category";
import { getUser, getCart, clearSession } from "@/utils/session";
import { getWishlistIds } from "@/utils/wishlist";

export default {
  name: "ClientLayout",
  data() {
    return {
      user: null,
      cartCount: 0,
      wishlistCount: 0,
      searchKeyword: "",
      searchResults: [],
      searchOpen: false,
      allProducts: [],
      allCategories: [],
    };
  },
  computed: {
    displayName() {
      if (!this.user) return "";
      return this.user.fullname || this.user.email || "Khách hàng";
    },
    userInitial() {
      return this.displayName.trim().charAt(0).toUpperCase() || "U";
    },
    roleLabel() {
      const role = this.user?.role?.toLowerCase();
      if (role === "admin") return "Quản trị viên";
      if (role === "staff") return "Nhân viên";
      return "Khách hàng";
    },
    canAccessAdmin() {
      return ["admin", "staff"].includes(this.user?.role?.toLowerCase());
    },
  },
  watch: {
    "$route.fullPath"() {
      this.closeSearch();
    },
  },
  mounted() {
    this.loadUser();
    this.loadCartCount();
    this.loadWishlistCount();
    this.loadAllProducts();
    this.loadCategories();
    window.addEventListener("cart-updated", this.loadCartCount);
    window.addEventListener("wishlist-updated", this.loadWishlistCount);
    window.addEventListener("user-logged-in", this.loadUser);
  },
  beforeUnmount() {
    window.removeEventListener("cart-updated", this.loadCartCount);
    window.removeEventListener("wishlist-updated", this.loadWishlistCount);
    window.removeEventListener("user-logged-in", this.loadUser);
  },
  methods: {
    loadUser() {
      this.user = getUser();
    },
    loadCartCount() {
      const cart = getCart();
      this.cartCount = cart.reduce((total, item) => total + Number(item.quantity || 1), 0);
    },
    loadWishlistCount() {
      this.wishlistCount = getWishlistIds().length;
    },
    updateCartCount() {
      this.loadCartCount();
    },
    async loadAllProducts() {
      try {
        const res = await getActiveProducts();
        this.allProducts = res.data || [];
      } catch (e) {
        console.error("Lỗi tải sản phẩm:", e);
      }
    },
    async loadCategories() {
      try {
        const res = await getCategories();
        this.allCategories = (res.data || []).filter((item) => item.status !== "inactive");
      } catch (e) {
        console.error("Lỗi tải danh mục:", e);
      }
    },
    handleSearch() {
      const keyword = this.searchKeyword.toLowerCase().trim();
      this.searchOpen = true;
      if (!keyword) {
        this.searchResults = [];
        return;
      }
      this.searchResults = this.allProducts
        .filter((p) => String(p.name || "").toLowerCase().includes(keyword))
        .slice(0, 6);
    },
    submitSearch() {
      if (this.searchResults.length > 0) this.goToProduct(this.searchResults[0].id);
    },
    clearSearch() {
      this.searchKeyword = "";
      this.searchResults = [];
      this.searchOpen = false;
      this.$refs.searchInput?.focus();
    },
    closeSearch() {
      this.searchOpen = false;
    },
    goToProduct(id) {
      this.searchKeyword = "";
      this.searchResults = [];
      this.searchOpen = false;
      this.$router.push(`/product/${id}`);
    },
    formatPrice(price) {
      return Number(price || 0).toLocaleString("vi-VN");
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
.client-shell { background: var(--surface); }
.fw-black { font-weight: 900; }
.store-header { position: sticky; top: 0; z-index: 1030; }
.topline { background: #071a3a; color: #bfd4f5; font-size: .74rem; }
.main-header { background: linear-gradient(105deg, #0b3fa9 0%, #075ee8 55%, #0d48b7 100%); box-shadow: 0 8px 26px rgba(7, 65, 170, .22); }
.header-grid { display: grid; grid-template-columns: auto minmax(260px, 1fr) auto; gap: 22px; align-items: center; }
.brand-lockup { display: flex; align-items: center; gap: 11px; flex-shrink: 0; }
.logo-mark { width: 46px; height: 46px; display: grid; place-items: center; border-radius: 15px; background: rgba(255,255,255,.13); border: 1px solid rgba(255,255,255,.26); box-shadow: inset 0 1px 0 rgba(255,255,255,.22); font-size: 1.4rem; }
.brand-copy { line-height: 1.05; }
.brand-name { font-size: 1.05rem; font-weight: 900; letter-spacing: .045em; }
.brand-sub { margin-top: 4px; font-size: .58rem; letter-spacing: .09em; opacity: .76; }
.search-wrapper { position: relative; width: 100%; max-width: 640px; justify-self: center; }
.store-search { height: 48px; padding: 0 46px 0 45px; border-radius: 16px; border: 1px solid rgba(255,255,255,.16); background: #fff; box-shadow: 0 7px 20px rgba(2, 32, 92, .14); font-size: .9rem; }
.store-search:focus { border-color: #bfdbfe; box-shadow: 0 0 0 4px rgba(191,219,254,.24), 0 7px 20px rgba(2, 32, 92, .14); }
.search-leading { position: absolute; left: 17px; top: 50%; transform: translateY(-50%); color: #64748b; z-index: 2; }
.search-clear { position: absolute; right: 9px; top: 50%; transform: translateY(-50%); width: 32px; height: 32px; display: grid; place-items: center; border: 0; border-radius: 50%; background: #f1f5f9; color: #64748b; }
.search-results { position: absolute; top: calc(100% + 8px); left: 0; right: 0; z-index: 1080; overflow: hidden; border-radius: 16px; background: #fff; border: 1px solid #e2e8f0; box-shadow: 0 20px 50px rgba(15,23,42,.18); }
.search-results-header { padding: 10px 12px 6px; color: #64748b; font-size: .7rem; font-weight: 800; letter-spacing: .07em; }
.search-result-item { display: flex; align-items: center; gap: 11px; padding: 10px 12px; border: 0; border-top: 1px solid #f1f5f9; background: #fff; transition: background .15s ease; }
.search-result-item:hover { background: #f8fafc; }
.result-img { width: 54px; height: 54px; object-fit: contain; border-radius: 10px; background: #f8fafc; }
.result-name { color: #1e293b; font-size: .85rem; font-weight: 700; }
.result-price { color: #dc2626; font-size: .83rem; font-weight: 800; }
.result-stock { color: #15803d; font-size: .68rem; font-weight: 700; }
.search-empty { color: #64748b; font-size: .85rem; }
.header-actions { display: flex; align-items: center; justify-content: flex-end; gap: 7px; }
.header-action { min-height: 46px; display: flex; align-items: center; gap: 7px; padding: 6px 9px; border-radius: 13px; color: #fff; text-decoration: none; background: transparent; font-size: .78rem; font-weight: 700; transition: background .16s ease; }
.header-action:hover, .header-action.router-link-active { color: #fff; background: rgba(255,255,255,.12); }
.action-icon { font-size: 1.38rem; line-height: 1; }
.action-badge { position: absolute; top: -8px; right: -11px; min-width: 19px; height: 19px; padding: 0 5px; display: grid; place-items: center; border-radius: 999px; background: #ef4444; border: 2px solid #075ee8; color: #fff; font-size: .6rem; font-weight: 900; }
.user-action { background: rgba(255,255,255,.09); }
.avatar-circle { width: 34px; height: 34px; display: grid; place-items: center; flex: 0 0 34px; border-radius: 50%; color: #0b3fa9; background: #fff; font-weight: 900; }
.user-name { max-width: 110px; }
.account-menu { min-width: 240px; border-radius: 14px; padding: 8px; }
.account-menu .dropdown-item { border-radius: 8px; padding: 9px 10px; font-size: .86rem; }
.sub-nav-bar { border-color: #e6ebf2 !important; }
.sub-nav-scroll { display: flex; align-items: center; gap: 6px; overflow-x: auto; scrollbar-width: none; }
.sub-nav-scroll::-webkit-scrollbar { display: none; }
.sub-nav-item { display: inline-flex; align-items: center; gap: 6px; flex: 0 0 auto; padding: 8px 11px; border-radius: 10px; color: #334155 !important; text-decoration: none; font-size: .8rem; font-weight: 700; transition: all .15s ease; }
.sub-nav-item:hover, .sub-nav-item.router-link-active { color: var(--brand-primary) !important; background: #eff6ff; }
.sub-nav-item i { color: var(--brand-primary); }
.nav-count { min-width: 18px; height: 18px; display: grid; place-items: center; padding: 0 4px; border-radius: 999px; color: #fff; background: var(--brand-primary); font-size: .62rem; }
.sub-nav-spacer { flex: 1 1 auto; }
.sub-nav-message { align-items: center; gap: 5px; white-space: nowrap; color: #b45309; font-size: .74rem; font-weight: 700; }
.category-menu { min-width: 240px; border-radius: 14px; padding: 8px; max-height: 360px; overflow-y: auto; }
.category-menu .dropdown-item { border-radius: 8px; font-size: .84rem; }
.site-footer { background: #081426; color: #fff; }
.footer-newsletter { background: linear-gradient(90deg, rgba(37,99,235,.16), rgba(30,64,175,.05)); }
.newsletter-icon { width: 52px; height: 52px; display: grid; place-items: center; border-radius: 16px; background: #1d4ed8; color: #fff; font-size: 1.3rem; }
.footer-muted { color: #94a3b8; }
.footer-logo { width: 42px; height: 42px; display: grid; place-items: center; border-radius: 13px; background: #1d4ed8; font-size: 1.2rem; }
.footer-brand-sub { font-size: .62rem; letter-spacing: .08em; }
.footer-links a { color: #94a3b8; text-decoration: none; font-size: .83rem; transition: color .15s ease; }
.footer-links a:hover { color: #fff; }
.social-button { width: 36px; height: 36px; display: grid; place-items: center; border-radius: 10px; color: #cbd5e1; background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.08); }
.contact-row { display: flex; align-items: center; gap: 9px; margin-bottom: 10px; color: #94a3b8; font-size: .83rem; }
.contact-row i { width: 30px; height: 30px; display: grid; place-items: center; flex: 0 0 30px; border-radius: 9px; color: #93c5fd; background: rgba(37,99,235,.13); }

@media (max-width: 991.98px) {
  .header-grid { grid-template-columns: auto 1fr auto; gap: 10px; }
  .search-wrapper { grid-column: 1 / -1; grid-row: 2; max-width: none; }
  .header-actions { gap: 2px; }
  .header-action { padding: 5px 7px; }
}

@media (max-width: 575.98px) {
  .main-header .container { padding-left: 12px; padding-right: 12px; }
  .header-grid { grid-template-columns: auto 1fr; }
  .header-actions { justify-self: end; }
  .brand-lockup { gap: 0; }
  .logo-mark { width: 42px; height: 42px; border-radius: 13px; }
  .header-action { min-height: 40px; }
  .action-icon { font-size: 1.22rem; }
  .store-search { height: 44px; border-radius: 14px; }
  .sub-nav-scroll { padding-left: 12px; padding-right: 12px; }
}
</style>
