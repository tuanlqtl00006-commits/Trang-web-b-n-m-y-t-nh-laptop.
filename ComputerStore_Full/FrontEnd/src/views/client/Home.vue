<template>
  <div>
    <!-- Hero Banner (Single Static Banner) -->
    <div class="asus-hero-banner mb-4 p-4 p-md-5 d-flex align-items-center justify-content-between position-relative overflow-hidden rounded-4 shadow-sm">
      <div class="banner-content z-2 text-white max-w-600">
        <div class="d-inline-flex align-items-center gap-2 px-3 py-1 bg-warning text-dark rounded-pill fw-bold text-uppercase small mb-2 shadow-sm">
          <i class="bi bi-mortarboard-fill"></i> MÙA KHAI TRƯỜNG 2026
        </div>
        <h1 class="banner-headline fw-black mb-1 text-uppercase">BACK TO SCHOOL</h1>
        <h3 class="banner-subheadline fw-bold text-warning mb-3">ĐẠI TIỆC CHÀO TRƯỜNG - SẮM LAPTOP XỈN</h3>
        <p class="banner-desc text-light opacity-90 mb-4 fs-5">
          Chương trình ưu đãi cực hot: <span class="fw-bold text-warning bg-dark px-2 py-1 rounded border border-warning"><i class="bi bi-gift-fill me-1"></i> MUA LAPTOP TẶNG NGAY CẶP BALO CAO CẤP</span> cho mọi đơn hàng học sinh - sinh viên!
        </p>
        <div class="d-flex flex-wrap align-items-center gap-3">
          <button @click="$router.push('/products')" class="btn btn-warning text-dark px-4 py-2 fw-bold rounded-pill shadow">
            MUA NGAY HÔM NAY <i class="bi bi-arrow-right ms-1"></i>
          </button>
          <div class="text-light small border-start border-light ps-3">
            <div class="fw-bold"><i class="bi bi-truck me-1"></i> Giao hàng hỏa tốc toàn quốc</div>
            <div class="opacity-75"><i class="bi bi-shield-check me-1"></i> Bảo hành chính hãng 24 tháng</div>
          </div>
        </div>
      </div>
      <!-- Decorative Gift Badge -->
      <div class="banner-graphic d-none d-md-flex flex-column align-items-center justify-content-center z-2">
        <div class="voucher-card p-3 rounded-4 bg-white text-dark shadow-lg text-center border border-warning">
          <span class="badge bg-danger mb-1 text-white"><i class="bi bi-bag-check-fill me-1"></i> QUÀ TẶNG KÈM</span>
          <div class="fs-5 fw-black text-primary mt-1">BALO CAO CẤP</div>
          <div class="small text-success fw-bold">Trị giá 450.000đ</div>
          <div class="badge bg-warning text-dark mt-1">MIỄN PHÍ 100%</div>
        </div>
      </div>
    </div>

    <!-- Featured Product Showcase Auto Slider (ASUS Style) -->
    <div v-if="featuredProducts.length > 0" class="showcase-container mb-5 p-4 bg-white rounded-4 shadow-sm border">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
          <h4 class="fw-bold mb-1 text-primary d-flex align-items-center gap-2">
            <i class="bi bi-fire text-danger"></i> Sản Phẩm Bán Chạy Nổi Bật
          </h4>
          <p class="text-muted small mb-0">Tự động lướt giới thiệu sản phẩm mới nhất của cửa hàng</p>
        </div>
        <div class="d-flex gap-2">
          <button class="btn btn-outline-secondary btn-sm rounded-circle p-2 d-flex align-items-center justify-content-center" style="width:36px; height:36px;" @click="prevShowcase">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-chevron-left" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/></svg>
          </button>
          <button class="btn btn-outline-secondary btn-sm rounded-circle p-2 d-flex align-items-center justify-content-center" style="width:36px; height:36px;" @click="nextShowcase">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-chevron-right" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M4.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L10.293 8 4.646 2.354a.5.5 0 0 1 0-.708z"/></svg>
          </button>
        </div>
      </div>

      <!-- Slider Track -->
      <div class="showcase-slider-wrapper overflow-hidden" @mouseenter="pauseAutoSlide" @mouseleave="startAutoSlide">
        <div class="showcase-track d-flex gap-3 transition-transform" :style="{ transform: `translateX(-${showcaseIndex * (100 / showcaseVisibleCount)}%)` }">
          <div v-for="p in featuredProducts" :key="'feat-' + p.id" class="showcase-card-col" :style="{ flex: `0 0 calc(${100 / showcaseVisibleCount}% - 12px)` }">
            <div class="card card-product h-100 border-0 shadow-sm hover-up" role="button" @click="goDetail(p.id)">
              <div class="badge-discount position-absolute top-0 start-0 m-2 bg-success text-white px-2 py-1 rounded small fw-bold" style="z-index: 2;">
                Tặng Cặp Balo
              </div>
              <img :src="productImage(p)" :alt="p.name" class="card-img-top p-3" style="height: 190px; object-fit: contain;" />
              <div class="card-body d-flex flex-column justify-content-between p-3">
                <div>
                  <h6 class="card-title text-truncate fw-bold mb-2" :title="p.name">{{ p.name }}</h6>
                  <p class="price-text fw-bold fs-5 text-danger mb-1">{{ formatPrice(p.price) }}₫</p>
                  <p class="small text-muted mb-2"><i class="bi bi-gift text-warning me-1"></i> Kèm balo laptop chính hãng</p>
                </div>
                <button class="btn btn-primary btn-sm w-100 rounded-pill fw-semibold mt-2">Xem chi tiết sản phẩm</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Dots Indicators -->
      <div class="d-flex justify-content-center gap-2 mt-3">
        <span v-for="n in maxShowcaseIndex + 1" :key="n" 
              class="dot-indicator" 
              :class="{ active: (n - 1) === showcaseIndex }"
              @click="showcaseIndex = n - 1"></span>
      </div>
    </div>

    <!-- Filter thanh -->
    <div class="d-flex flex-wrap gap-2 mb-4 align-items-center">
      <select v-model="filterCategory" class="form-select w-auto" @change="applyFilter">
        <option value="">Tất cả danh mục</option>
        <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>
      <select v-model="filterBrand" class="form-select w-auto" @change="applyFilter">
        <option value="">Tất cả thương hiệu</option>
        <option v-for="b in brands" :key="b.id" :value="b.id">{{ b.brandName }}</option>
      </select>
      <select v-model="sortOption" class="form-select w-auto" @change="applyFilter">
        <option value="">Sắp xếp</option>
        <option value="price-asc">Giá tăng dần</option>
        <option value="price-desc">Giá giảm dần</option>
        <option value="name-asc">Tên A-Z</option>
      </select>
      <span class="text-muted ms-auto">{{ filteredProducts.length }} sản phẩm</span>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border" role="status"></div>
    </div>

    <div v-else class="row g-3">
      <div class="col-6 col-md-4 col-lg-3" v-for="p in pagedProducts" :key="p.id">
        <div class="card card-product h-100" role="button" @click="goDetail(p.id)">
          <img :src="productImage(p)" :alt="p.name" class="card-img-top" />
          <div class="card-body">
            <h6 class="card-title text-truncate" :title="p.name">{{ p.name }}</h6>
            <p class="price-text mb-1">{{ formatPrice(p.price) }}₫</p>
            <p class="small text-muted mb-0">
              <span v-if="p.stock > 0" class="text-success">Còn hàng ({{ p.stock }})</span>
              <span v-else class="text-danger">Hết hàng</span>
            </p>
          </div>
        </div>
      </div>
      <div v-if="filteredProducts.length === 0" class="text-center text-muted py-5">
        Không tìm thấy sản phẩm nào.
      </div>
    </div>

    <AppPagination v-if="!loading" v-model="page" :total-items="filteredProducts.length" :per-page="perPage" />
  </div>
</template>

<script>
import { getActiveProducts } from "@/api/product";
import { getActiveCategories } from "@/api/category";
import { getActiveBrands } from "@/api/brand";
import AppPagination from "@/components/Pagination.vue";

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
      sortOption: "",
      filteredProducts: [],
      loading: true,
      page: 1,
      perPage: 12,
      showcaseIndex: 0,
      showcaseVisibleCount: 4,
      autoSlideTimer: null,
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
  },
  async mounted() {
    await this.loadData();
    this.updateVisibleCount();
    window.addEventListener("resize", this.updateVisibleCount);
    this.startAutoSlide();
  },
  beforeUnmount() {
    this.pauseAutoSlide();
    window.removeEventListener("resize", this.updateVisibleCount);
  },
  methods: {
    updateVisibleCount() {
      const width = window.innerWidth;
      if (width < 576) this.showcaseVisibleCount = 1;
      else if (width < 992) this.showcaseVisibleCount = 2;
      else if (width < 1200) this.showcaseVisibleCount = 3;
      else this.showcaseVisibleCount = 4;
    },
    startAutoSlide() {
      this.pauseAutoSlide();
      this.autoSlideTimer = setInterval(() => {
        this.nextShowcase();
      }, 3000);
    },
    pauseAutoSlide() {
      if (this.autoSlideTimer) {
        clearInterval(this.autoSlideTimer);
        this.autoSlideTimer = null;
      }
    },
    nextShowcase() {
      if (this.showcaseIndex < this.maxShowcaseIndex) {
        this.showcaseIndex++;
      } else {
        this.showcaseIndex = 0;
      }
    },
    prevShowcase() {
      if (this.showcaseIndex > 0) {
        this.showcaseIndex--;
      } else {
        this.showcaseIndex = this.maxShowcaseIndex;
      }
    },
    async loadData() {
      this.loading = true;
      try {
        const [pRes, cRes, bRes] = await Promise.all([
          getActiveProducts(),
          getActiveCategories(),
          getActiveBrands(),
        ]);
        this.products = pRes.data;
        this.categories = cRes.data;
        this.brands = bRes.data;
        this.applyFilter();
      } catch (e) {
        console.error("Lỗi tải dữ liệu trang chủ:", e);
      } finally {
        this.loading = false;
      }
    },
    applyFilter() {
      let list = [...this.products];
      if (this.filterCategory) {
        list = list.filter((p) => p.categoryId === Number(this.filterCategory));
      }
      if (this.filterBrand) {
        list = list.filter((p) => p.brandId === Number(this.filterBrand));
      }
      if (this.sortOption === "price-asc") {
        list.sort((a, b) => a.price - b.price);
      } else if (this.sortOption === "price-desc") {
        list.sort((a, b) => b.price - a.price);
      } else if (this.sortOption === "name-asc") {
        list.sort((a, b) => a.name.localeCompare(b.name));
      }
      this.filteredProducts = list;
      this.page = 1;
    },
    goDetail(id) {
      this.$router.push(`/product/${id}`);
    },
    productImage(p) {
      if (p.image) return encodeURI(p.image);
      return "";
    },
    formatPrice(price) {
      if (!price) return "0";
      return price.toLocaleString("vi-VN");
    },
  },
};
</script>

<style scoped>
.asus-hero-banner {
  background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 40%, #1e293b 100%);
  min-height: 320px;
  position: relative;
}
.asus-hero-banner::before {
  content: "";
  position: absolute;
  top: -50%;
  right: -10%;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(234, 179, 8, 0.25) 0%, rgba(37, 99, 235, 0.15) 50%, transparent 70%);
  pointer-events: none;
}
.banner-headline {
  font-size: clamp(2rem, 5vw, 3.2rem);
  letter-spacing: 0.04em;
  background: linear-gradient(90deg, #ffffff 0%, #fef08a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  line-height: 1.1;
}
.banner-subheadline {
  font-size: clamp(1.1rem, 2.5vw, 1.6rem);
}
.max-w-600 {
  max-width: 650px;
}
.fw-black {
  font-weight: 900;
}
.voucher-card {
  transform: rotate(3deg);
  min-width: 170px;
  transition: transform 0.3s ease;
}
.voucher-card:hover {
  transform: rotate(0deg) scale(1.05);
}
.banner {
  background: linear-gradient(120deg, var(--brand-dark) 0%, var(--brand-dark-soft) 55%, var(--brand-primary-dark) 100%);
  min-height: 320px;
}
.transition-transform {
  transition: transform 0.5s cubic-bezier(0.25, 1, 0.5, 1);
}
.showcase-container {
  background: #ffffff;
  border-radius: var(--radius-lg);
}
.dot-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #d1d5db;
  cursor: pointer;
  transition: all 0.3s ease;
}
.dot-indicator.active {
  width: 28px;
  border-radius: 6px;
  background-color: var(--brand-primary);
}
.hover-up {
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}
.hover-up:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1) !important;
}
</style>
