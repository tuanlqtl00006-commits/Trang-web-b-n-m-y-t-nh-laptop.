<template>
  <div>
    <h4 class="fw-bold mb-4">{{ categoryName }}</h4>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border" role="status"></div>
    </div>

    <div v-else class="row g-3">
      <div class="col-6 col-md-4 col-lg-3" v-for="p in products" :key="p.id">
        <div class="card card-product h-100" role="button" @click="goDetail(p.id)">
          <img :src="productImage(p)" :alt="p.name" class="card-img-top" />
          <div class="card-body">
            <h6 class="card-title text-truncate" :title="p.name">{{ p.name }}</h6>
            <p class="price-text mb-1">{{ formatPrice(p.price) }}₫</p>
          </div>
        </div>
      </div>
      <div v-if="products.length === 0" class="text-center text-muted py-5">
        Không có sản phẩm nào trong danh mục này.
      </div>
    </div>
  </div>
</template>

<script>
import { getProductsByCategory } from "@/api/product";
import { getCategoryById } from "@/api/category";

export default {
  name: "ProductsByCategory",
  data() {
    return {
      products: [],
      categoryName: "",
      loading: true,
    };
  },
  watch: {
    "$route.params.id"() {
      this.loadData();
    },
  },
  mounted() {
    this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      const id = this.$route.params.id;
      try {
        const [pRes, cRes] = await Promise.all([
          getProductsByCategory(id),
          getCategoryById(id),
        ]);
        this.products = pRes.data;
        this.categoryName = cRes.data?.name || "Danh mục";
      } catch (e) {
        console.error("Lỗi tải sản phẩm theo danh mục:", e);
      } finally {
        this.loading = false;
      }
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
