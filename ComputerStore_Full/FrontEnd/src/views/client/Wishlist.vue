<template>
  <section class="wishlist-page">
    <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
      <div>
        <span class="eyebrow">DANH SÁCH CỦA BẠN</span>
        <h2 class="fw-black mb-1">Sản phẩm yêu thích</h2>
        <p class="text-muted mb-0">Lưu lại laptop bạn đang quan tâm để xem lại nhanh hơn.</p>
      </div>
      <button v-if="wishlistProducts.length" class="btn btn-outline-danger rounded-pill px-3" @click="clearAll">
        <i class="bi bi-trash3 me-1"></i> Xóa tất cả
      </button>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <div v-else-if="wishlistProducts.length === 0" class="empty-wishlist text-center bg-white rounded-4 p-5 shadow-sm border">
      <div class="empty-icon mx-auto mb-3"><i class="bi bi-heart"></i></div>
      <h4 class="fw-bold">Chưa có sản phẩm yêu thích</h4>
      <p class="text-muted mb-4">Nhấn biểu tượng trái tim ở sản phẩm để thêm vào danh sách này.</p>
      <router-link to="/" class="btn btn-primary rounded-pill px-4">
        <i class="bi bi-shop me-1"></i> Khám phá sản phẩm
      </router-link>
    </div>

    <div v-else class="row g-3 g-lg-4">
      <div class="col-6 col-md-4 col-xl-3" v-for="p in wishlistProducts" :key="p.id">
        <article class="card card-product h-100 wishlist-card position-relative">
          <button class="wishlist-button active" type="button" title="Bỏ yêu thích" @click.stop="removeFavorite(p.id)">
            <i class="bi bi-heart-fill"></i>
          </button>

          <div class="product-image-wrap" role="button" @click="goDetail(p.id)">
            <img :src="productImage(p)" :alt="p.name" class="card-img-top" />
          </div>

          <div class="card-body d-flex flex-column">
            <div class="d-flex gap-2 mb-2 flex-wrap">
              <span v-if="p.ssd" class="spec-chip"><i class="bi bi-device-ssd"></i>{{ p.ssd }}</span>
              <span v-if="p.screen" class="spec-chip"><i class="bi bi-display"></i>{{ p.screen }}</span>
            </div>
            <h6 class="card-title product-name mb-2" role="button" @click="goDetail(p.id)">{{ p.name }}</h6>
            <p class="price-text fs-5 mb-1">{{ formatPrice(p.price) }}₫</p>
            <div class="small mb-3">
              <span v-if="p.stock > 0" class="stock-pill in-stock"><i class="bi bi-check-circle-fill"></i> Còn {{ p.stock }} sản phẩm</span>
              <span v-else class="stock-pill out-stock"><i class="bi bi-x-circle-fill"></i> Hết hàng</span>
            </div>
            <button class="btn btn-outline-primary rounded-pill mt-auto" @click="goDetail(p.id)">
              Xem chi tiết <i class="bi bi-arrow-right ms-1"></i>
            </button>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script>
import { getActiveProducts } from "@/api/product";
import { clearWishlist, getWishlistIds, toggleWishlist } from "@/utils/wishlist";
import { toast } from "@/utils/toast";

export default {
  name: "Wishlist",
  data() {
    return {
      products: [],
      wishlistIds: [],
      loading: true,
    };
  },
  computed: {
    wishlistProducts() {
      const order = new Map(this.wishlistIds.map((id, index) => [id, index]));
      return this.products
        .filter((p) => order.has(Number(p.id)))
        .sort((a, b) => order.get(Number(a.id)) - order.get(Number(b.id)));
    },
  },
  mounted() {
    this.loadData();
    window.addEventListener("wishlist-updated", this.syncWishlist);
  },
  beforeUnmount() {
    window.removeEventListener("wishlist-updated", this.syncWishlist);
  },
  methods: {
    async loadData() {
      this.loading = true;
      this.wishlistIds = getWishlistIds();
      try {
        const res = await getActiveProducts();
        this.products = res.data || [];
      } catch (e) {
        console.error("Lỗi tải sản phẩm yêu thích:", e);
      } finally {
        this.loading = false;
      }
    },
    syncWishlist(event) {
      this.wishlistIds = event?.detail || getWishlistIds();
    },
    removeFavorite(id) {
      toggleWishlist(id);
      toast.info("Đã bỏ khỏi danh sách yêu thích");
    },
    clearAll() {
      clearWishlist();
      toast.info("Đã xóa danh sách yêu thích");
    },
    goDetail(id) {
      this.$router.push(`/product/${id}`);
    },
    productImage(p) {
      return p.image ? encodeURI(p.image) : "";
    },
    formatPrice(price) {
      return Number(price || 0).toLocaleString("vi-VN");
    },
  },
};
</script>

<style scoped>
.eyebrow {
  display: inline-block;
  color: var(--brand-primary);
  font-size: 0.74rem;
  font-weight: 800;
  letter-spacing: 0.12em;
  margin-bottom: 4px;
}
.fw-black { font-weight: 900; }
.empty-icon {
  width: 76px;
  height: 76px;
  border-radius: 24px;
  display: grid;
  place-items: center;
  font-size: 2rem;
  color: #e11d48;
  background: #fff1f2;
}
.wishlist-card { overflow: visible; }
.product-image-wrap { overflow: hidden; border-radius: 14px 14px 0 0; }
.product-name {
  line-height: 1.4;
  min-height: 2.8em;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.wishlist-button {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 4;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  border: 1px solid #fecdd3;
  background: #fff;
  color: #e11d48;
  display: grid;
  place-items: center;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.12);
}
.spec-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 0.72rem;
  color: #475569;
  background: #f1f5f9;
  padding: 4px 7px;
  border-radius: 999px;
}
.stock-pill { display: inline-flex; gap: 5px; align-items: center; font-weight: 600; }
.in-stock { color: #15803d; }
.out-stock { color: #dc2626; }
</style>
