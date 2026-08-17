<template>
  <section class="category-page">
    <nav aria-label="breadcrumb" class="mb-3">
      <ol class="breadcrumb small mb-0">
        <li class="breadcrumb-item"><router-link to="/">Trang chủ</router-link></li>
        <li class="breadcrumb-item active">{{ categoryName }}</li>
      </ol>
    </nav>

    <div class="category-heading rounded-4 p-4 p-lg-5 mb-4 position-relative overflow-hidden">
      <div class="position-relative z-2">
        <span class="category-kicker">DANH MỤC SẢN PHẨM</span>
        <h2 class="fw-black text-white mb-2">{{ categoryName }}</h2>
        <p class="text-white-50 mb-0">Khám phá các mẫu laptop đang có tại ComputerStore.</p>
      </div>
      <i class="bi bi-laptop category-watermark"></i>
    </div>

    <div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
      <span class="text-muted"><strong class="text-dark">{{ products.length }}</strong> sản phẩm</span>
      <select v-model="sortOption" class="form-select w-auto rounded-pill" @change="sortProducts">
        <option value="">Sắp xếp mặc định</option>
        <option value="price-asc">Giá tăng dần</option>
        <option value="price-desc">Giá giảm dần</option>
        <option value="name-asc">Tên A-Z</option>
      </select>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else class="row g-3 g-lg-4">
      <div class="col-6 col-md-4 col-xl-3" v-for="p in pagedProducts" :key="p.id">
        <article class="card card-product h-100 position-relative">
          <button class="wishlist-button" :class="{ active: isFavorite(p.id) }" type="button" @click.stop="toggleFavorite(p.id)">
            <i :class="isFavorite(p.id) ? 'bi bi-heart-fill' : 'bi bi-heart'"></i>
          </button>
          <div class="image-zone" role="button" @click="goDetail(p.id)">
            <img :src="productImage(p)" :alt="p.name" class="card-img-top" />
          </div>
          <div class="card-body d-flex flex-column">
            <div class="d-flex flex-wrap gap-1 mb-2">
              <span v-if="p.ssd" class="spec-chip"><i class="bi bi-device-ssd"></i>{{ p.ssd }}</span>
              <span v-if="p.screen" class="spec-chip"><i class="bi bi-display"></i>{{ shortSpec(p.screen) }}</span>
            </div>
            <h6 class="product-name fw-bold mb-2" :title="p.name" role="button" @click="goDetail(p.id)">{{ p.name }}</h6>
            <p class="price-text fs-5 mb-1">{{ formatPrice(p.price) }}₫</p>
            <p class="small mb-3">
              <span v-if="p.stock > 0" class="text-success fw-semibold"><i class="bi bi-check-circle-fill me-1"></i>Còn hàng</span>
              <span v-else class="text-danger fw-semibold"><i class="bi bi-x-circle-fill me-1"></i>Hết hàng</span>
            </p>
            <button class="btn btn-primary rounded-pill mt-auto" @click="goDetail(p.id)">Xem chi tiết</button>
          </div>
        </article>
      </div>

      <div v-if="products.length === 0" class="col-12">
        <div class="text-center bg-white border rounded-4 p-5">
          <i class="bi bi-inbox fs-1 text-muted"></i>
          <h5 class="fw-bold mt-3">Danh mục chưa có sản phẩm</h5>
          <router-link to="/" class="btn btn-outline-primary rounded-pill mt-2">Về trang chủ</router-link>
        </div>
      </div>
    </div>

    <AppPagination v-if="!loading && products.length" v-model="page" :total-items="products.length" :per-page="perPage" />
  </section>
</template>

<script>
import { getProductsByCategory } from "@/api/product";
import { getCategoryById } from "@/api/category";
import AppPagination from "@/components/Pagination.vue";
import { getWishlistIds, toggleWishlist } from "@/utils/wishlist";
import { toast } from "@/utils/toast";

export default {
  name: "ProductsByCategory",
  components: { AppPagination },
  data() {
    return {
      products: [],
      categoryName: "",
      loading: true,
      page: 1,
      perPage: 12,
      sortOption: "",
      wishlistIds: [],
    };
  },
  computed: {
    pagedProducts() {
      const start = (this.page - 1) * this.perPage;
      return this.products.slice(start, start + this.perPage);
    },
  },
  watch: {
    "$route.params.id"() {
      this.loadData();
    },
  },
  mounted() {
    this.wishlistIds = getWishlistIds();
    window.addEventListener("wishlist-updated", this.syncWishlist);
    this.loadData();
  },
  beforeUnmount() {
    window.removeEventListener("wishlist-updated", this.syncWishlist);
  },
  methods: {
    syncWishlist(event) {
      this.wishlistIds = event?.detail || getWishlistIds();
    },
    isFavorite(id) {
      return this.wishlistIds.includes(Number(id));
    },
    toggleFavorite(id) {
      const wasFavorite = this.isFavorite(id);
      this.wishlistIds = toggleWishlist(id);
      toast.info(wasFavorite ? "Đã bỏ khỏi yêu thích" : "Đã thêm vào yêu thích");
    },
    async loadData() {
      this.loading = true;
      this.page = 1;
      this.sortOption = "";
      const id = this.$route.params.id;
      try {
        const [pRes, cRes] = await Promise.all([getProductsByCategory(id), getCategoryById(id)]);
        this.products = pRes.data || [];
        this.categoryName = cRes.data?.name || "Danh mục";
      } catch (e) {
        console.error("Lỗi tải sản phẩm theo danh mục:", e);
      } finally {
        this.loading = false;
      }
    },
    sortProducts() {
      const list = [...this.products];
      if (this.sortOption === "price-asc") list.sort((a, b) => a.price - b.price);
      else if (this.sortOption === "price-desc") list.sort((a, b) => b.price - a.price);
      else if (this.sortOption === "name-asc") list.sort((a, b) => a.name.localeCompare(b.name, "vi"));
      else list.sort((a, b) => Number(a.id) - Number(b.id));
      this.products = list;
      this.page = 1;
    },
    goDetail(id) {
      this.$router.push(`/product/${id}`);
    },
    productImage(p) {
      return p.image ? encodeURI(p.image) : "";
    },
    shortSpec(value) {
      const text = String(value || "");
      return text.length > 18 ? `${text.slice(0, 16)}…` : text;
    },
    formatPrice(price) {
      return Number(price || 0).toLocaleString("vi-VN");
    },
  },
};
</script>

<style scoped>
.fw-black { font-weight: 900; }
.category-heading { background: linear-gradient(120deg, #071a3a 0%, #0d47b5 70%, #2563eb 100%); min-height: 160px; display: flex; align-items: center; }
.category-heading::before { content: ""; position: absolute; width: 220px; height: 220px; right: 12%; top: -80px; border-radius: 50%; background: rgba(255,255,255,.08); }
.category-kicker { color: #bfdbfe; font-size: .72rem; font-weight: 800; letter-spacing: .1em; }
.category-watermark { position: absolute; right: 5%; bottom: -28px; color: rgba(255,255,255,.1); font-size: 9rem; transform: rotate(-8deg); }
.image-zone { overflow: hidden; border-radius: 14px 14px 0 0; background: #fff; }
.image-zone img { transition: transform .25s ease; }
.card-product:hover .image-zone img { transform: scale(1.035); }
.product-name { line-height: 1.4; min-height: 2.8em; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.wishlist-button { position: absolute; right: 10px; top: 10px; z-index: 3; width: 38px; height: 38px; display: grid; place-items: center; border-radius: 50%; border: 1px solid #e2e8f0; color: #64748b; background: rgba(255,255,255,.94); box-shadow: 0 6px 18px rgba(15,23,42,.1); }
.wishlist-button.active, .wishlist-button:hover { color: #e11d48; background: #fff1f2; border-color: #fecdd3; }
.spec-chip { display: inline-flex; align-items: center; gap: 4px; font-size: .69rem; color: #475569; background: #f1f5f9; padding: 4px 7px; border-radius: 999px; }
</style>
