<template>
  <div>
    <!-- Banner -->
    <div class="banner rounded-4 mb-4 p-5 text-white">
      <h2 class="fw-bold">Cửa hàng máy tính - Laptop chính hãng</h2>
      <p class="mb-0">Giá tốt nhất - Bảo hành uy tín - Giao hàng toàn quốc</p>
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
      perPage: 12, // grid auto-adds a new page once more products than this exist
    };
  },
  computed: {
    pagedProducts() {
      const start = (this.page - 1) * this.perPage;
      return this.filteredProducts.slice(start, start + this.perPage);
    },
  },
  async mounted() {
    await this.loadData();
  },
  methods: {
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
.banner {
  background: linear-gradient(120deg, var(--brand-dark) 0%, var(--brand-dark-soft) 55%, var(--brand-primary-dark) 100%);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  position: relative;
  overflow: hidden;
}
.banner::after {
  content: "";
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 85% 20%, rgba(37, 99, 235, 0.35), transparent 55%);
  pointer-events: none;
}
.banner h2,
.banner p {
  color: #ffffff;
  position: relative;
}
</style>
