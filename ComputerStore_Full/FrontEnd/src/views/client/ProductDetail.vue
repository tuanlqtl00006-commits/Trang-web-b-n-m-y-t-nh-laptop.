<template>
  <div v-if="product" class="product-detail-page">
    <nav aria-label="breadcrumb" class="mb-3">
      <ol class="breadcrumb small mb-0">
        <li class="breadcrumb-item"><router-link to="/">Trang chủ</router-link></li>
        <li class="breadcrumb-item active text-truncate" aria-current="page">{{ product.name }}</li>
      </ol>
    </nav>

    <section class="product-detail-card bg-white border rounded-4 shadow-sm p-3 p-lg-4 mb-4">
      <div class="row g-4 g-xl-5 align-items-start">
        <div class="col-lg-5">
          <div class="image-panel position-relative rounded-4 p-3 p-md-4 text-center">
            <span v-if="product.stock > 0" class="availability-badge in-stock"><i class="bi bi-check-circle-fill"></i> Còn hàng</span>
            <span v-else class="availability-badge out-stock"><i class="bi bi-x-circle-fill"></i> Hết hàng</span>
            <button
              class="detail-wishlist-button"
              :class="{ active: favorite }"
              type="button"
              :title="favorite ? 'Bỏ yêu thích' : 'Thêm yêu thích'"
              @click="toggleFavorite"
            >
              <i :class="favorite ? 'bi bi-heart-fill' : 'bi bi-heart'"></i>
            </button>
            <img :src="product.image" :alt="product.name" class="img-fluid product-main-image" />
          </div>

          <div class="row g-2 mt-2">
            <div class="col-4">
              <div class="mini-benefit"><i class="bi bi-shield-check"></i><span>Chính hãng</span></div>
            </div>
            <div class="col-4">
              <div class="mini-benefit"><i class="bi bi-truck"></i><span>Giao nhanh</span></div>
            </div>
            <div class="col-4">
              <div class="mini-benefit"><i class="bi bi-headset"></i><span>Hỗ trợ</span></div>
            </div>
          </div>
        </div>

        <div class="col-lg-7">
          <span class="detail-kicker">LAPTOP CHÍNH HÃNG</span>
          <h2 class="fw-black product-title mt-1 mb-2">{{ product.name }}</h2>
          <div class="d-flex flex-wrap align-items-center gap-2 mb-3">
            <span v-if="brandName" class="meta-pill"><i class="bi bi-award"></i>{{ brandName }}</span>
            <span v-if="product.stock > 0" class="meta-pill text-success"><i class="bi bi-box-seam"></i>Còn {{ product.stock }} sản phẩm</span>
          </div>

          <div class="price-panel rounded-4 p-3 mb-4">
            <div class="text-muted small fw-semibold">Giá bán</div>
            <div class="price-text product-price">{{ formatPrice(product.price) }}₫</div>
            <div class="small text-success mt-1"><i class="bi bi-gift-fill me-1"></i>Tặng balo laptop cao cấp khi mua sản phẩm</div>
          </div>

          <h6 class="fw-bold mb-3">Cấu hình nổi bật</h6>
          <div class="spec-grid mb-4">
            <div class="spec-item"><i class="bi bi-cpu"></i><div><small>CPU</small><strong>{{ cpuName || "Đang cập nhật" }}</strong></div></div>
            <div class="spec-item"><i class="bi bi-memory"></i><div><small>RAM</small><strong>{{ ramName || "Đang cập nhật" }}</strong></div></div>
            <div class="spec-item"><i class="bi bi-device-ssd"></i><div><small>Ổ cứng</small><strong>{{ product.ssd || "Đang cập nhật" }}</strong></div></div>
            <div class="spec-item"><i class="bi bi-gpu-card"></i><div><small>Đồ họa</small><strong>{{ product.gpu || "Đang cập nhật" }}</strong></div></div>
            <div class="spec-item"><i class="bi bi-display"></i><div><small>Màn hình</small><strong>{{ product.screen || "Đang cập nhật" }}</strong></div></div>
            <div class="spec-item"><i class="bi bi-windows"></i><div><small>Hệ điều hành</small><strong>{{ product.os || "Đang cập nhật" }}</strong></div></div>
          </div>

          <div class="purchase-panel border-top pt-4">
            <div class="d-flex flex-wrap align-items-center gap-3 mb-3">
              <span class="fw-semibold">Số lượng</span>
              <div class="quantity-stepper">
                <button type="button" :disabled="quantity <= 1" @click="changeQuantity(-1)"><i class="bi bi-dash"></i></button>
                <input type="number" v-model.number="quantity" min="1" :max="product.stock" @change="normalizeQuantity" />
                <button type="button" :disabled="quantity >= product.stock || product.stock <= 0" @click="changeQuantity(1)"><i class="bi bi-plus"></i></button>
              </div>
            </div>

            <div class="d-grid d-sm-flex gap-2">
              <button class="btn btn-outline-primary btn-lg rounded-pill px-4" :disabled="product.stock <= 0" @click="addToCart">
                <i class="bi bi-cart-plus me-1"></i> Thêm vào giỏ
              </button>
              <button class="btn btn-primary btn-lg rounded-pill px-5" :disabled="product.stock <= 0" @click="buyNow">
                Mua ngay <i class="bi bi-arrow-right ms-1"></i>
              </button>
              <button class="btn btn-light btn-lg rounded-circle favorite-secondary" :class="{ active: favorite }" type="button" @click="toggleFavorite" title="Yêu thích">
                <i :class="favorite ? 'bi bi-heart-fill' : 'bi bi-heart'"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="description-card bg-white border rounded-4 shadow-sm p-4 p-lg-5">
      <div class="d-flex align-items-center gap-2 mb-3">
        <div class="section-icon"><i class="bi bi-file-earmark-text"></i></div>
        <div>
          <span class="detail-kicker">THÔNG TIN SẢN PHẨM</span>
          <h4 class="fw-black mb-0">Mô tả chi tiết</h4>
        </div>
      </div>
      <p class="description-text mb-0" style="white-space: pre-line">{{ product.description || "Thông tin mô tả đang được cập nhật." }}</p>
    </section>
  </div>

  <div v-else class="detail-loading text-center py-5 bg-white rounded-4 border shadow-sm">
    <div class="spinner-border text-primary" role="status"></div>
    <div class="text-muted mt-2">Đang tải thông tin sản phẩm...</div>
  </div>
</template>

<script>
import { getProductById } from "@/api/product";
import { getCpuById } from "@/api/cpu";
import { getRamById } from "@/api/ram";
import { getBrandById } from "@/api/brand";
import { getCart, setCart } from "@/utils/session";
import { isWishlisted, toggleWishlist } from "@/utils/wishlist";
import { toast } from "@/utils/toast";

export default {
  name: "ProductDetail",
  data() {
    return {
      product: null,
      quantity: 1,
      cpuName: "",
      ramName: "",
      brandName: "",
      favorite: false,
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
      this.cpuName = "";
      this.ramName = "";
      this.brandName = "";
      const id = this.$route.params.id;
      try {
        const res = await getProductById(id);
        this.product = res.data;
        this.quantity = 1;
        this.favorite = isWishlisted(this.product.id);

        const lookups = [];
        if (this.product.cpuId) {
          lookups.push(getCpuById(this.product.cpuId).then((r) => { this.cpuName = r.data.cpuName; }).catch(() => {}));
        }
        if (this.product.ramId) {
          lookups.push(getRamById(this.product.ramId).then((r) => { this.ramName = r.data.ramName; }).catch(() => {}));
        }
        if (this.product.brandId) {
          lookups.push(getBrandById(this.product.brandId).then((r) => { this.brandName = r.data.brandName; }).catch(() => {}));
        }
        await Promise.all(lookups);
      } catch (e) {
        console.error("Lỗi tải chi tiết sản phẩm:", e);
        toast.error("Không thể tải thông tin sản phẩm");
      }
    },
    toggleFavorite() {
      if (!this.product) return;
      const wasFavorite = this.favorite;
      toggleWishlist(this.product.id);
      this.favorite = !wasFavorite;
      toast.info(wasFavorite ? "Đã bỏ khỏi yêu thích" : "Đã thêm vào yêu thích");
    },
    changeQuantity(delta) {
      this.quantity += delta;
      this.normalizeQuantity();
    },
    normalizeQuantity() {
      const max = Math.max(1, Number(this.product?.stock || 1));
      const value = Number.isFinite(Number(this.quantity)) ? Number(this.quantity) : 1;
      this.quantity = Math.max(1, Math.min(Math.floor(value), max));
    },
    addToCart() {
      if (!this.product || this.product.stock <= 0) return;
      this.normalizeQuantity();
      const cart = getCart();
      const existing = cart.find((i) => Number(i.id) === Number(this.product.id));
      if (existing) {
        existing.quantity = Math.min(Number(this.product.stock), Number(existing.quantity || 0) + this.quantity);
        existing.stock = this.product.stock;
      } else {
        cart.push({
          id: this.product.id,
          name: this.product.name,
          price: this.product.price,
          image: this.product.image,
          stock: this.product.stock,
          quantity: Math.min(this.quantity, this.product.stock),
        });
      }
      setCart(cart);
      window.dispatchEvent(new Event("cart-updated"));
      toast.success("Đã thêm vào giỏ hàng!");
    },
    buyNow() {
      this.addToCart();
      this.$router.push("/cart");
    },
    formatPrice(price) {
      return Number(price || 0).toLocaleString("vi-VN");
    },
  },
};
</script>

<style scoped>
.fw-black { font-weight: 900; }
.product-detail-card, .description-card { border-color: #e2e8f0 !important; }
.image-panel { min-height: 430px; display: grid; place-items: center; background: linear-gradient(145deg, #f8fbff, #f5f7fb); border: 1px solid #e7edf6; overflow: hidden; }
.image-panel::after { content: ""; position: absolute; width: 260px; height: 260px; border-radius: 50%; background: radial-gradient(circle, rgba(37,99,235,.08), transparent 68%); z-index: 0; }
.product-main-image { max-height: 350px; width: 100%; object-fit: contain; position: relative; z-index: 1; transition: transform .3s ease; }
.image-panel:hover .product-main-image { transform: scale(1.035); }
.availability-badge { position: absolute; left: 14px; top: 14px; z-index: 3; display: inline-flex; align-items: center; gap: 5px; padding: 6px 10px; border-radius: 999px; font-size: .75rem; font-weight: 800; }
.availability-badge.in-stock { background: #dcfce7; color: #15803d; }
.availability-badge.out-stock { background: #fee2e2; color: #b91c1c; }
.detail-wishlist-button { position: absolute; right: 14px; top: 14px; z-index: 3; width: 42px; height: 42px; display: grid; place-items: center; border-radius: 50%; border: 1px solid #e2e8f0; background: rgba(255,255,255,.94); color: #64748b; font-size: 1.1rem; box-shadow: 0 6px 18px rgba(15,23,42,.08); transition: all .18s ease; }
.detail-wishlist-button.active, .detail-wishlist-button:hover { color: #e11d48; background: #fff1f2; border-color: #fecdd3; }
.mini-benefit { min-height: 62px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 3px; text-align: center; background: #fff; border: 1px solid #e2e8f0; border-radius: 14px; color: #475569; font-size: .75rem; font-weight: 700; }
.mini-benefit i { color: var(--brand-primary); font-size: 1rem; }
.detail-kicker { color: var(--brand-primary); font-size: .72rem; font-weight: 800; letter-spacing: .1em; }
.product-title { line-height: 1.25; }
.meta-pill { display: inline-flex; align-items: center; gap: 5px; padding: 6px 9px; border-radius: 999px; background: #f8fafc; border: 1px solid #e2e8f0; color: #475569; font-size: .78rem; font-weight: 700; }
.price-panel { background: linear-gradient(90deg, #fff7ed, #fff); border: 1px solid #fed7aa; }
.product-price { font-size: clamp(1.8rem, 4vw, 2.45rem); font-weight: 900; letter-spacing: -.03em; }
.spec-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 10px; }
.spec-item { min-height: 72px; display: flex; align-items: center; gap: 10px; border: 1px solid #e2e8f0; border-radius: 14px; padding: 10px 12px; background: #fbfdff; }
.spec-item > i { width: 34px; height: 34px; display: grid; place-items: center; flex: 0 0 34px; border-radius: 10px; color: var(--brand-primary); background: #eff6ff; }
.spec-item small { display: block; color: #64748b; font-size: .7rem; margin-bottom: 1px; }
.spec-item strong { display: block; font-size: .84rem; line-height: 1.25; color: #1e293b; word-break: break-word; }
.quantity-stepper { display: inline-flex; align-items: center; overflow: hidden; border: 1px solid #cbd5e1; border-radius: 12px; background: #fff; }
.quantity-stepper button { width: 40px; height: 40px; border: 0; background: #f8fafc; color: #334155; }
.quantity-stepper button:disabled { opacity: .4; }
.quantity-stepper input { width: 58px; height: 40px; text-align: center; border: 0; border-left: 1px solid #e2e8f0; border-right: 1px solid #e2e8f0; outline: none; font-weight: 700; }
.quantity-stepper input::-webkit-outer-spin-button, .quantity-stepper input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
.favorite-secondary { width: 50px; flex: 0 0 50px; color: #64748b; }
.favorite-secondary.active { color: #e11d48; background: #fff1f2; }
.section-icon { width: 48px; height: 48px; display: grid; place-items: center; flex: 0 0 48px; border-radius: 15px; color: var(--brand-primary); background: #eff6ff; font-size: 1.2rem; }
.description-text { color: #475569; line-height: 1.8; }

@media (max-width: 575.98px) {
  .image-panel { min-height: 300px; }
  .product-main-image { max-height: 260px; }
  .spec-grid { grid-template-columns: 1fr; }
  .favorite-secondary { border-radius: 999px !important; width: 100%; }
}
</style>
