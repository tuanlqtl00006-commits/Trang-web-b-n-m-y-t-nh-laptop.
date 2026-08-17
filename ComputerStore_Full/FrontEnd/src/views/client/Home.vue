<template>
  <div class="home-page">
    <!-- Hero -->
    <section class="hero-banner mb-4 p-4 p-md-5 d-flex align-items-center justify-content-between position-relative overflow-hidden rounded-4 shadow-sm">
      <div class="hero-grid"></div>
      <div class="banner-content position-relative z-2 text-white max-w-650">
        <div class="d-inline-flex align-items-center gap-2 px-3 py-1 promo-pill rounded-pill fw-bold text-uppercase small mb-3">
          <i class="bi bi-mortarboard-fill"></i> MÙA KHAI TRƯỜNG 2026
        </div>
        <h1 class="banner-headline fw-black mb-2 text-uppercase">BACK TO SCHOOL</h1>
        <h3 class="banner-subheadline fw-bold mb-3">Laptop mạnh hơn. Học tập nhanh hơn.</h3>
        <p class="banner-desc text-light mb-4 fs-5 opacity-90">
          Sắm laptop chính hãng, nhận <strong class="text-warning">balo cao cấp miễn phí</strong> và nhiều quyền lợi hậu mãi tại ComputerStore.
        </p>
        <div class="d-flex flex-wrap align-items-center gap-3">
          <button @click="scrollToProducts" class="btn btn-warning text-dark px-4 py-2 fw-bold rounded-pill shadow-sm">
            Xem laptop ngay <i class="bi bi-arrow-down-right ms-1"></i>
          </button>
          <div class="hero-support text-light small">
            <div class="fw-bold"><i class="bi bi-truck me-1"></i> Giao hàng toàn quốc</div>
            <div class="opacity-75"><i class="bi bi-shield-check me-1"></i> Bảo hành chính hãng</div>
          </div>
        </div>
      </div>

      <div class="banner-graphic d-none d-lg-flex flex-column align-items-center justify-content-center position-relative z-2">
        <div class="laptop-orbit">
          <div class="orbit-ring"></div>
          <div class="voucher-card p-4 rounded-4 bg-white text-dark shadow-lg text-center">
            <div class="gift-icon mx-auto mb-2"><i class="bi bi-gift-fill"></i></div>
            <span class="badge bg-danger mb-2">QUÀ TẶNG KÈM</span>
            <div class="fs-4 fw-black text-primary">BALO CAO CẤP</div>
            <div class="small text-success fw-bold">Trị giá 450.000đ</div>
            <div class="badge bg-warning text-dark mt-2">MIỄN PHÍ 100%</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Service benefits -->
    <section class="benefit-strip row g-2 g-md-3 mb-4">
      <div class="col-6 col-lg-3" v-for="item in benefits" :key="item.title">
        <div class="benefit-item h-100 bg-white border rounded-4 p-3 d-flex align-items-center gap-3">
          <div class="benefit-icon"><i :class="item.icon"></i></div>
          <div>
            <div class="fw-bold small">{{ item.title }}</div>
            <div class="text-muted benefit-desc">{{ item.desc }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured products -->
    <section v-if="featuredProducts.length > 0" class="showcase-container mb-5 p-3 p-md-4 bg-white rounded-4 shadow-sm border">
      <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
        <div>
          <span class="section-kicker"><i class="bi bi-lightning-charge-fill"></i> ĐÁNG CHÚ Ý</span>
          <h4 class="fw-black mb-1">Laptop nổi bật</h4>
          <p class="text-muted small mb-0">Những sản phẩm mới đang được quan tâm tại cửa hàng.</p>
        </div>
        <div class="d-flex gap-2">
          <button aria-label="Sản phẩm trước" class="slider-button" @click="prevShowcase"><i class="bi bi-chevron-left"></i></button>
          <button aria-label="Sản phẩm sau" class="slider-button" @click="nextShowcase"><i class="bi bi-chevron-right"></i></button>
        </div>
      </div>

      <div class="showcase-slider-wrapper overflow-hidden" @mouseenter="pauseAutoSlide" @mouseleave="startAutoSlide">
        <div class="showcase-track d-flex gap-3 transition-transform" :style="{ transform: `translateX(-${showcaseIndex * (100 / showcaseVisibleCount)}%)` }">
          <div v-for="p in featuredProducts" :key="'feat-' + p.id" class="showcase-card-col" :style="{ flex: `0 0 calc(${100 / showcaseVisibleCount}% - 12px)` }">
            <article class="card card-product featured-card h-100 border-0 shadow-sm position-relative" role="button" @click="goDetail(p.id)">
              <span class="promo-badge"><i class="bi bi-gift me-1"></i>Tặng balo</span>
              <button
                class="wishlist-button"
                :class="{ active: isFavorite(p.id) }"
                type="button"
                :title="isFavorite(p.id) ? 'Bỏ yêu thích' : 'Thêm yêu thích'"
                @click.stop="toggleFavorite(p.id)"
              >
                <i :class="isFavorite(p.id) ? 'bi bi-heart-fill' : 'bi bi-heart'"></i>
              </button>
              <div class="product-image-zone">
                <img :src="productImage(p)" :alt="p.name" class="card-img-top" />
              </div>
              <div class="card-body d-flex flex-column p-3">
                <h6 class="product-name fw-bold mb-2" :title="p.name">{{ p.name }}</h6>
                <div class="d-flex flex-wrap gap-1 mb-2">
                  <span v-if="p.ssd" class="spec-chip"><i class="bi bi-device-ssd"></i>{{ p.ssd }}</span>
                  <span v-if="p.screen" class="spec-chip"><i class="bi bi-display"></i>{{ p.screen }}</span>
                </div>
                <p class="price-text fw-black fs-5 mb-1">{{ formatPrice(p.price) }}₫</p>
                <p class="small mb-3">
                  <span v-if="p.stock > 0" class="stock-pill in-stock"><i class="bi bi-check-circle-fill"></i> Còn hàng</span>
                  <span v-else class="stock-pill out-stock"><i class="bi bi-x-circle-fill"></i> Hết hàng</span>
                </p>
                <button class="btn btn-outline-primary btn-sm w-100 rounded-pill fw-semibold mt-auto">Xem chi tiết</button>
              </div>
            </article>
          </div>
        </div>
      </div>

      <div class="d-flex justify-content-center gap-2 mt-3" v-if="maxShowcaseIndex > 0">
        <button
          v-for="n in maxShowcaseIndex + 1"
          :key="n"
          class="dot-indicator border-0 p-0"
          :class="{ active: n - 1 === showcaseIndex }"
          :aria-label="`Chuyển đến nhóm ${n}`"
          @click="showcaseIndex = n - 1"
        ></button>
      </div>
    </section>

    <!-- Products -->
    <section id="product-section" class="product-section scroll-mt">
      <div class="d-flex flex-wrap align-items-end justify-content-between gap-2 mb-3">
        <div>
          <span class="section-kicker"><i class="bi bi-grid-fill"></i> CỬA HÀNG</span>
          <h3 class="fw-black mb-1">Chọn laptop phù hợp với bạn</h3>
          <p class="text-muted mb-0">Lọc nhanh theo nhu cầu và mức giá.</p>
        </div>
        <span class="result-counter"><strong>{{ filteredProducts.length }}</strong> sản phẩm</span>
      </div>

      <div class="filter-panel bg-white border rounded-4 p-3 p-lg-4 mb-4 shadow-sm">
        <div class="row g-3 align-items-end">
          <div class="col-sm-6 col-lg-3">
            <label class="form-label">Danh mục</label>
            <select v-model="filterCategory" class="form-select" @change="applyFilter">
              <option value="">Tất cả danh mục</option>
              <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="col-sm-6 col-lg-3">
            <label class="form-label">Thương hiệu</label>
            <select v-model="filterBrand" class="form-select" @change="applyFilter">
              <option value="">Tất cả thương hiệu</option>
              <option v-for="b in brands" :key="b.id" :value="b.id">{{ b.brandName }}</option>
            </select>
          </div>
          <div class="col-sm-6 col-lg-3">
            <label class="form-label">Khoảng giá</label>
            <select v-model="priceRange" class="form-select" @change="applyFilter">
              <option value="">Tất cả mức giá</option>
              <option value="under-15">Dưới 15 triệu</option>
              <option value="15-25">15 - 25 triệu</option>
              <option value="25-40">25 - 40 triệu</option>
              <option value="over-40">Trên 40 triệu</option>
            </select>
          </div>
          <div class="col-sm-6 col-lg-3">
            <label class="form-label">Sắp xếp</label>
            <select v-model="sortOption" class="form-select" @change="applyFilter">
              <option value="">Mặc định</option>
              <option value="price-asc">Giá tăng dần</option>
              <option value="price-desc">Giá giảm dần</option>
              <option value="name-asc">Tên A-Z</option>
            </select>
          </div>
          <div class="col-12 d-flex flex-wrap align-items-center justify-content-between gap-2 pt-1">
            <label class="stock-toggle m-0">
              <input v-model="inStockOnly" type="checkbox" @change="applyFilter" />
              <span><i class="bi bi-box-seam me-1"></i> Chỉ hiển thị sản phẩm còn hàng</span>
            </label>
            <button v-if="hasActiveFilters" class="btn btn-sm btn-light border rounded-pill px-3" @click="resetFilters">
              <i class="bi bi-arrow-counterclockwise me-1"></i> Xóa bộ lọc
            </button>
          </div>
        </div>
      </div>

      <div v-if="loading" class="text-center py-5">
        <div class="spinner-border text-primary" role="status"></div>
        <div class="text-muted small mt-2">Đang tải sản phẩm...</div>
      </div>

      <div v-else class="row g-3 g-lg-4">
        <div class="col-6 col-md-4 col-xl-3" v-for="p in pagedProducts" :key="p.id">
          <article class="card card-product product-card h-100 position-relative">
            <button
              class="wishlist-button"
              :class="{ active: isFavorite(p.id) }"
              type="button"
              :title="isFavorite(p.id) ? 'Bỏ yêu thích' : 'Thêm yêu thích'"
              @click.stop="toggleFavorite(p.id)"
            >
              <i :class="isFavorite(p.id) ? 'bi bi-heart-fill' : 'bi bi-heart'"></i>
            </button>

            <div class="product-image-zone" role="button" @click="goDetail(p.id)">
              <img :src="productImage(p)" :alt="p.name" class="card-img-top" />
              <span v-if="p.stock > 0 && p.stock <= 5" class="low-stock-badge">Sắp hết</span>
            </div>

            <div class="card-body d-flex flex-column">
              <div class="d-flex flex-wrap gap-1 mb-2">
                <span v-if="p.ssd" class="spec-chip"><i class="bi bi-device-ssd"></i>{{ p.ssd }}</span>
                <span v-if="p.gpu" class="spec-chip"><i class="bi bi-gpu-card"></i>{{ shortSpec(p.gpu) }}</span>
                <span v-if="p.screen" class="spec-chip"><i class="bi bi-display"></i>{{ shortSpec(p.screen) }}</span>
              </div>
              <h6 class="card-title product-name mb-2" :title="p.name" role="button" @click="goDetail(p.id)">{{ p.name }}</h6>
              <p class="price-text fs-5 mb-1">{{ formatPrice(p.price) }}₫</p>
              <div class="small mb-3">
                <span v-if="p.stock > 0" class="stock-pill in-stock"><i class="bi bi-check-circle-fill"></i> Còn {{ p.stock }} sản phẩm</span>
                <span v-else class="stock-pill out-stock"><i class="bi bi-x-circle-fill"></i> Hết hàng</span>
              </div>
              <button class="btn btn-primary rounded-pill mt-auto" @click="goDetail(p.id)">
                Xem sản phẩm <i class="bi bi-arrow-right-short"></i>
              </button>
            </div>
          </article>
        </div>

        <div v-if="filteredProducts.length === 0" class="col-12">
          <div class="empty-result text-center bg-white rounded-4 border p-5">
            <div class="empty-result-icon mx-auto mb-3"><i class="bi bi-search"></i></div>
            <h5 class="fw-bold">Không tìm thấy sản phẩm phù hợp</h5>
            <p class="text-muted mb-3">Hãy thử thay đổi danh mục, thương hiệu hoặc khoảng giá.</p>
            <button class="btn btn-outline-primary rounded-pill px-4" @click="resetFilters">Xóa bộ lọc</button>
          </div>
        </div>
      </div>

      <AppPagination v-if="!loading && filteredProducts.length" v-model="page" :total-items="filteredProducts.length" :per-page="perPage" />
    </section>
  </div>
</template>

<script>
import { getActiveProducts } from "@/api/product";
import { getActiveCategories } from "@/api/category";
import { getActiveBrands } from "@/api/brand";
import AppPagination from "@/components/Pagination.vue";
import { getWishlistIds, toggleWishlist } from "@/utils/wishlist";
import { toast } from "@/utils/toast";

export default {
  name: "Home",
  components: { AppPagination },
  data() {
    return {
      products: [],
      categories: [],
      brands: [],
      filterCategory: "",
      filterBrand: "",
      priceRange: "",
      sortOption: "",
      inStockOnly: false,
      filteredProducts: [],
      wishlistIds: [],
      loading: true,
      page: 1,
      perPage: 12,
      showcaseIndex: 0,
      showcaseVisibleCount: 4,
      autoSlideTimer: null,
      benefits: [
        { icon: "bi bi-shield-check", title: "Hàng chính hãng", desc: "Nguồn gốc rõ ràng" },
        { icon: "bi bi-truck", title: "Giao hàng nhanh", desc: "Đóng gói an toàn" },
        { icon: "bi bi-arrow-repeat", title: "Hỗ trợ đổi trả", desc: "Quy trình minh bạch" },
        { icon: "bi bi-headset", title: "Tư vấn tận tâm", desc: "Hỗ trợ chọn cấu hình" },
      ],
    };
  },
  computed: {
    pagedProducts() {
      const start = (this.page - 1) * this.perPage;
      return this.filteredProducts.slice(start, start + this.perPage);
    },
    featuredProducts() {
      return this.products.slice(0, 10);
    },
    maxShowcaseIndex() {
      return Math.max(0, this.featuredProducts.length - this.showcaseVisibleCount);
    },
    hasActiveFilters() {
      return Boolean(this.filterCategory || this.filterBrand || this.priceRange || this.sortOption || this.inStockOnly);
    },
  },
  async mounted() {
    this.wishlistIds = getWishlistIds();
    window.addEventListener("wishlist-updated", this.syncWishlist);
    await this.loadData();
    this.updateVisibleCount();
    window.addEventListener("resize", this.updateVisibleCount);
    this.startAutoSlide();
  },
  beforeUnmount() {
    this.pauseAutoSlide();
    window.removeEventListener("resize", this.updateVisibleCount);
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
    scrollToProducts() {
      document.getElementById("product-section")?.scrollIntoView({ behavior: "smooth", block: "start" });
    },
    updateVisibleCount() {
      const width = window.innerWidth;
      if (width < 576) this.showcaseVisibleCount = 1;
      else if (width < 992) this.showcaseVisibleCount = 2;
      else if (width < 1200) this.showcaseVisibleCount = 3;
      else this.showcaseVisibleCount = 4;
      this.showcaseIndex = Math.min(this.showcaseIndex, this.maxShowcaseIndex);
    },
    startAutoSlide() {
      this.pauseAutoSlide();
      if (this.maxShowcaseIndex <= 0) return;
      this.autoSlideTimer = setInterval(this.nextShowcase, 3500);
    },
    pauseAutoSlide() {
      if (this.autoSlideTimer) {
        clearInterval(this.autoSlideTimer);
        this.autoSlideTimer = null;
      }
    },
    nextShowcase() {
      this.showcaseIndex = this.showcaseIndex < this.maxShowcaseIndex ? this.showcaseIndex + 1 : 0;
    },
    prevShowcase() {
      this.showcaseIndex = this.showcaseIndex > 0 ? this.showcaseIndex - 1 : this.maxShowcaseIndex;
    },
    async loadData() {
      this.loading = true;
      try {
        const [pRes, cRes, bRes] = await Promise.all([
          getActiveProducts(),
          getActiveCategories(),
          getActiveBrands(),
        ]);
        this.products = pRes.data || [];
        this.categories = cRes.data || [];
        this.brands = bRes.data || [];
        this.applyFilter();
      } catch (e) {
        console.error("Lỗi tải dữ liệu trang chủ:", e);
      } finally {
        this.loading = false;
      }
    },
    applyFilter() {
      let list = [...this.products];

      if (this.filterCategory) list = list.filter((p) => p.categoryId === Number(this.filterCategory));
      if (this.filterBrand) list = list.filter((p) => p.brandId === Number(this.filterBrand));
      if (this.inStockOnly) list = list.filter((p) => Number(p.stock || 0) > 0);

      if (this.priceRange) {
        list = list.filter((p) => {
          const price = Number(p.price || 0);
          if (this.priceRange === "under-15") return price < 15000000;
          if (this.priceRange === "15-25") return price >= 15000000 && price <= 25000000;
          if (this.priceRange === "25-40") return price > 25000000 && price <= 40000000;
          if (this.priceRange === "over-40") return price > 40000000;
          return true;
        });
      }

      if (this.sortOption === "price-asc") list.sort((a, b) => a.price - b.price);
      else if (this.sortOption === "price-desc") list.sort((a, b) => b.price - a.price);
      else if (this.sortOption === "name-asc") list.sort((a, b) => a.name.localeCompare(b.name, "vi"));

      this.filteredProducts = list;
      this.page = 1;
    },
    resetFilters() {
      this.filterCategory = "";
      this.filterBrand = "";
      this.priceRange = "";
      this.sortOption = "";
      this.inStockOnly = false;
      this.applyFilter();
    },
    goDetail(id) {
      this.$router.push(`/product/${id}`);
    },
    productImage(p) {
      return p.image ? encodeURI(p.image) : "";
    },
    shortSpec(value) {
      const text = String(value || "").trim();
      return text.length > 20 ? `${text.slice(0, 18)}…` : text;
    },
    formatPrice(price) {
      return Number(price || 0).toLocaleString("vi-VN");
    },
  },
};
</script>

<style scoped>
.home-page { --hero-blue: #155eef; }
.fw-black { font-weight: 900; }
.max-w-650 { max-width: 650px; }
.scroll-mt { scroll-margin-top: 155px; }

.hero-banner {
  min-height: 360px;
  background:
    radial-gradient(circle at 83% 25%, rgba(96, 165, 250, 0.34), transparent 26%),
    linear-gradient(125deg, #071225 0%, #0c2354 52%, #1546ad 100%);
  isolation: isolate;
}
.hero-grid {
  position: absolute;
  inset: 0;
  opacity: 0.13;
  background-image:
    linear-gradient(rgba(255,255,255,.32) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,.32) 1px, transparent 1px);
  background-size: 36px 36px;
  mask-image: linear-gradient(to right, #000, transparent 90%);
}
.promo-pill { background: rgba(250, 204, 21, 0.95); color: #172554; box-shadow: 0 8px 24px rgba(250,204,21,.18); }
.banner-headline {
  font-size: clamp(2.5rem, 6vw, 4.25rem);
  letter-spacing: -0.03em;
  line-height: 0.95;
  background: linear-gradient(90deg, #fff 0%, #bfdbfe 55%, #fde68a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.banner-subheadline { color: #fde68a; font-size: clamp(1.15rem, 2.4vw, 1.65rem); }
.hero-support { border-left: 1px solid rgba(255,255,255,.35); padding-left: 1rem; }
.laptop-orbit { position: relative; width: 280px; height: 260px; display: grid; place-items: center; }
.orbit-ring { position: absolute; width: 250px; height: 250px; border-radius: 50%; border: 1px dashed rgba(255,255,255,.28); animation: spin 18s linear infinite; }
.orbit-ring::before, .orbit-ring::after { content: ""; position: absolute; width: 10px; height: 10px; border-radius: 50%; background: #fde047; box-shadow: 0 0 22px #fde047; }
.orbit-ring::before { top: 17px; left: 47px; }
.orbit-ring::after { right: 14px; bottom: 54px; }
@keyframes spin { to { transform: rotate(360deg); } }
.voucher-card { transform: rotate(2deg); min-width: 190px; border: 1px solid rgba(255,255,255,.5); }
.gift-icon { width: 52px; height: 52px; border-radius: 16px; display: grid; place-items: center; color: #fff; background: linear-gradient(135deg, #2563eb, #7c3aed); font-size: 1.4rem; }

.benefit-item { transition: transform .2s ease, box-shadow .2s ease; }
.benefit-item:hover { transform: translateY(-3px); box-shadow: var(--shadow-md); }
.benefit-icon { width: 44px; height: 44px; border-radius: 14px; display: grid; place-items: center; flex: 0 0 44px; background: #eff6ff; color: var(--brand-primary); font-size: 1.2rem; }
.benefit-desc { font-size: .77rem; }

.section-kicker { display: inline-flex; align-items: center; gap: 5px; color: var(--brand-primary); font-size: .72rem; font-weight: 800; letter-spacing: .1em; margin-bottom: 4px; }
.showcase-container { background: #fff; }
.slider-button { width: 38px; height: 38px; display: grid; place-items: center; border-radius: 50%; border: 1px solid #dbe3ef; color: #334155; background: #fff; transition: all .18s ease; }
.slider-button:hover { color: #fff; background: var(--brand-primary); border-color: var(--brand-primary); transform: translateY(-1px); }
.transition-transform { transition: transform .5s cubic-bezier(.25,1,.5,1); }
.featured-card:hover, .product-card:hover { transform: translateY(-6px); }
.dot-indicator { width: 9px; height: 9px; border-radius: 999px; background: #cbd5e1; transition: all .25s ease; }
.dot-indicator.active { width: 28px; background: var(--brand-primary); }

.product-image-zone { position: relative; overflow: hidden; border-radius: 14px 14px 0 0; background: linear-gradient(180deg, #fff, #f8fafc); }
.card-product .product-image-zone img { transition: transform .28s ease; }
.card-product:hover .product-image-zone img { transform: scale(1.035); }
.product-name { line-height: 1.4; min-height: 2.8em; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.promo-badge { position: absolute; top: 12px; left: 12px; z-index: 4; padding: 5px 9px; border-radius: 999px; background: #dcfce7; color: #15803d; font-size: .72rem; font-weight: 800; }
.low-stock-badge { position: absolute; left: 10px; bottom: 8px; padding: 4px 8px; border-radius: 999px; background: #fff7ed; color: #c2410c; border: 1px solid #fed7aa; font-size: .7rem; font-weight: 800; }
.wishlist-button { position: absolute; top: 10px; right: 10px; z-index: 5; width: 38px; height: 38px; border-radius: 50%; border: 1px solid #e2e8f0; background: rgba(255,255,255,.94); color: #64748b; display: grid; place-items: center; box-shadow: 0 6px 20px rgba(15,23,42,.10); transition: all .18s ease; }
.wishlist-button:hover, .wishlist-button.active { color: #e11d48; border-color: #fecdd3; background: #fff1f2; transform: scale(1.06); }
.spec-chip { display: inline-flex; align-items: center; gap: 4px; font-size: .69rem; color: #475569; background: #f1f5f9; border: 1px solid #e9eef5; padding: 4px 7px; border-radius: 999px; }
.stock-pill { display: inline-flex; align-items: center; gap: 5px; font-weight: 600; }
.in-stock { color: #15803d; }
.out-stock { color: #dc2626; }

.filter-panel { border-color: #e2e8f0 !important; }
.result-counter { color: #64748b; background: #fff; border: 1px solid #e2e8f0; padding: 7px 12px; border-radius: 999px; font-size: .86rem; }
.stock-toggle { display: inline-flex; align-items: center; cursor: pointer; }
.stock-toggle input { position: absolute; opacity: 0; pointer-events: none; }
.stock-toggle span { display: inline-flex; align-items: center; padding: 8px 12px; border-radius: 999px; color: #475569; background: #f8fafc; border: 1px solid #e2e8f0; font-size: .84rem; font-weight: 600; transition: all .18s ease; }
.stock-toggle input:checked + span { color: #166534; border-color: #86efac; background: #f0fdf4; box-shadow: inset 0 0 0 1px #bbf7d0; }
.empty-result-icon { width: 64px; height: 64px; border-radius: 20px; display: grid; place-items: center; font-size: 1.6rem; color: #64748b; background: #f1f5f9; }

@media (max-width: 767.98px) {
  .hero-banner { min-height: 320px; }
  .banner-desc { font-size: 1rem !important; }
  .hero-support { width: 100%; border-left: 0; padding-left: 0; }
  .benefit-desc { display: none; }
  .benefit-item { padding: .75rem !important; }
  .benefit-icon { width: 38px; height: 38px; flex-basis: 38px; border-radius: 12px; }
}

@media (prefers-reduced-motion: reduce) {
  .orbit-ring { animation: none; }
  .transition-transform { transition: none; }
}
</style>
