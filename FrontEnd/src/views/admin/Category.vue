<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold mb-0">Quản lý danh mục</h4>
      <button class="btn btn-dark" @click="openCreate">
        <i class="bi bi-plus-lg"></i> Thêm danh mục
      </button>
    </div>

    <table class="table align-middle bg-white">
      <thead>
        <tr>
          <th>ID</th>
          <th>Ảnh</th>
          <th>Tên danh mục</th>
          <th>Mô tả</th>
          <th>Trạng thái</th>
          <th style="width: 140px"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="c in items" :key="c.id">
          <td>{{ c.id }}</td>
          <td>
            <img
              v-if="imageUrl(c.img)"
              :src="imageUrl(c.img)"
              :alt="c.name"
              width="56"
              height="56"
              class="category-thumb"
            />
            <span v-else class="text-muted">—</span>
          </td>
          <td>{{ c.name }}</td>
          <td>{{ c.description }}</td>
          <td>
            <span class="badge" :class="c.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'">
              {{ c.status === "ACTIVE" ? "Hoạt động" : "Ngừng" }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-outline-primary me-1" @click="openEdit(c)">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn btn-sm btn-outline-danger" @click="remove(c.id)">
              <i class="bi bi-trash"></i>
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Modal -->
    <div v-if="showModal" class="modal-backdrop-custom" @click.self="showModal = false">
      <div class="modal-box">
        <h5 class="fw-bold mb-3">{{ editingId ? "Sửa danh mục" : "Thêm danh mục" }}</h5>
        <div class="mb-3">
          <label class="form-label">Tên danh mục</label>
          <input v-model="form.name" type="text" class="form-control" />
        </div>
        <div class="mb-3">
          <label class="form-label">Mô tả</label>
          <textarea v-model="form.description" class="form-control" rows="2"></textarea>
        </div>
        <div class="mb-3">
          <label class="form-label">Ảnh danh mục</label>
          <input
            v-model="form.img"
            type="text"
            class="form-control mb-2"
            placeholder="http://localhost:8080/uploads/category.jpg"
          />
          <img
            v-if="imageUrl(form.img)"
            :src="imageUrl(form.img)"
            :alt="form.name || 'category'"
            class="category-preview"
          />
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
import { getCategories, createCategory, updateCategory, deleteCategory } from "@/api/category";

export default {
  name: "AdminCategory",
  data() {
    return {
      items: [],
      showModal: false,
      editingId: null,
      form: { name: "", description: "", img: "", status: "ACTIVE" },
    };
  },
  mounted() {
    this.loadItems();
  },
  methods: {
    async loadItems() {
      const res = await getCategories();
      this.items = res.data;
    },
    openCreate() {
      this.editingId = null;
      this.form = { name: "", description: "", img: "", status: "ACTIVE" };
      this.showModal = true;
    },
    openEdit(item) {
      this.editingId = item.id;
      this.form = { ...item };
      this.showModal = true;
    },
    async save() {
      if (!this.form.name) {
        alert("Vui lòng nhập tên danh mục!");
        return;
      }
      try {
        if (this.editingId) {
          await updateCategory(this.editingId, this.form);
        } else {
          await createCategory(this.form);
        }
        this.showModal = false;
        await this.loadItems();
      } catch (e) {
        alert("Có lỗi xảy ra, vui lòng thử lại!");
      }
    },
    async remove(id) {
      if (!confirm("Bạn có chắc muốn xóa danh mục này?")) return;
      await deleteCategory(id);
      await this.loadItems();
    },
    imageUrl(path) {
      if (!path) return "";
      if (path.startsWith("http://") || path.startsWith("https://")) return encodeURI(path);
      if (path.startsWith("/uploads/")) return encodeURI(`http://localhost:8080${path}`);
      if (path.startsWith("uploads/")) return encodeURI(`http://localhost:8080/${path}`);
      return encodeURI(`http://localhost:8080/uploads/${path}`);
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
.category-thumb {
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}
.category-preview {
  width: 100%;
  max-height: 160px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}
</style>
