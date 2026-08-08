<template>
  <nav v-if="totalPages > 1" class="app-pagination" aria-label="Phân trang">
    <button
      class="page-btn"
      :disabled="modelValue === 1"
      @click="go(modelValue - 1)"
      aria-label="Trang trước"
    >
      <i class="bi bi-chevron-left"></i>
    </button>

    <button
      v-for="p in pages"
      :key="p"
      class="page-btn"
      :class="{ 'page-btn--active': p === modelValue, 'page-btn--dots': p === '...' }"
      :disabled="p === '...'"
      @click="go(p)"
    >
      {{ p }}
    </button>

    <button
      class="page-btn"
      :disabled="modelValue === totalPages"
      @click="go(modelValue + 1)"
      aria-label="Trang sau"
    >
      <i class="bi bi-chevron-right"></i>
    </button>
  </nav>
</template>

<script>
export default {
  name: "AppPagination",
  props: {
    modelValue: { type: Number, required: true },
    totalItems: { type: Number, required: true },
    perPage: { type: Number, default: 12 },
  },
  emits: ["update:modelValue"],
  computed: {
    totalPages() {
      return Math.max(1, Math.ceil(this.totalItems / this.perPage));
    },
    pages() {
      const total = this.totalPages;
      const current = this.modelValue;
      const delta = 1;
      const range = [];
      for (let i = Math.max(2, current - delta); i <= Math.min(total - 1, current + delta); i++) {
        range.push(i);
      }
      const result = [1];
      if (range.length && range[0] > 2) result.push("...");
      result.push(...range);
      if (range.length && range[range.length - 1] < total - 1) result.push("...");
      if (total > 1) result.push(total);
      return result;
    },
  },
  methods: {
    go(p) {
      if (p === "..." || p < 1 || p > this.totalPages || p === this.modelValue) return;
      this.$emit("update:modelValue", p);
    },
  },
};
</script>

<style scoped>
.app-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 24px;
  flex-wrap: wrap;
}
.page-btn {
  min-width: 38px;
  height: 38px;
  padding: 0 10px;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  background: #fff;
  color: #374151;
  font-weight: 600;
  font-size: 0.9rem;
  transition: all 0.15s ease;
}
.page-btn:hover:not(:disabled) {
  border-color: var(--brand-primary, #2563eb);
  color: var(--brand-primary, #2563eb);
}
.page-btn--active {
  background: var(--brand-primary, #2563eb);
  border-color: var(--brand-primary, #2563eb);
  color: #fff;
}
.page-btn--dots {
  border: none;
  background: transparent;
  cursor: default;
}
.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>
