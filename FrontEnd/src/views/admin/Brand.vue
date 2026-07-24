<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold mb-0">Quản lý thương hiệu</h4>
      <button class="btn btn-dark" @click="openCreate">
        <i class="bi bi-plus-lg"></i> Thêm thương hiệu
      </button>
    </div>

    <table class="table align-middle bg-white">
      <thead>
        <tr>
          <th>ID</th>
          <th>Tên thương hiệu</th>
          <th>Trạng thái</th>
          <th style="width: 140px"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="b in items" :key="b.id">
          <td>{{ b.id }}</td>
          <td>{{ b.brandName }}</td>
          <td>
            <span class="badge" :class="b.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'">
              {{ b.status === "ACTIVE" ? "Hoạt động" : "Ngừng" }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-outline-primary me-1" @click="openEdit(b)">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn btn-sm btn-outline-danger" @click="remove(b.id)">
              <i class="bi bi-trash"></i>
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showModal" class="modal-backdrop-custom" @click.self="showModal = false">
      <div class="modal-box">
        <h5 class="fw-bold mb-3">{{ editingId ? "Sửa thương hiệu" : "Thêm thương hiệu" }}</h5>
        <div class="mb-3">
          <label class="form-label">Tên thương hiệu</label>
          <input v-model="form.brandName" type="text" class="form-control" />
        </div>
        <div class="mb-3">
          <label class="form-label">Trạng thái</label>
          <select v-model="form.status" class="form-select">
            <option value="ACTIVE">Hoạt động</option>
            <option value="INACTIVE">Ngừng</option>
          </select>
        </div>
        <div class="d-flex justify-content-end gap-2">
          <button class="btn btn-secondary" @click="showModal = false">Hủy</button>
          <button class="btn btn-dark" @click="save">Lưu</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getBrands, createBrand, updateBrand, deleteBrand } from "@/api/brand";

export default {
  name: "AdminBrand",
  data() {
    return {
      items: [],
      showModal: false,
      editingId: null,
      form: { brandName: "", status: "ACTIVE" },
    };
  },
  mounted() {
    this.loadItems();
  },
  methods: {
    async loadItems() {
      const res = await getBrands();
      this.items = res.data;
    },
    openCreate() {
      this.editingId = null;
      this.form = { brandName: "", status: "ACTIVE" };
      this.showModal = true;
    },
    openEdit(item) {
      this.editingId = item.id;
      this.form = { ...item };
      this.showModal = true;
    },
    async save() {
      if (!this.form.brandName) {
        alert("Vui lòng nhập tên thương hiệu!");
        return;
      }
      if (this.editingId) {
        await updateBrand(this.editingId, this.form);
      } else {
        await createBrand(this.form);
      }
      this.showModal = false;
      await this.loadItems();
    },
    async remove(id) {
      if (!confirm("Bạn có chắc muốn xóa thương hiệu này?")) return;
      await deleteBrand(id);
      await this.loadItems();
    },
  },
};
</script>

<style scoped>
.modal-backdrop-custom {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
}
.modal-box {
  background: white;
  border-radius: 8px;
  padding: 24px;
  width: 420px;
  max-width: 90vw;
}
</style>
