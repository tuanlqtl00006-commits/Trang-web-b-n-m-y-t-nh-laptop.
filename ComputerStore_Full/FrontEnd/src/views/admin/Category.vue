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
        <tr v-for="c in pagedItems" :key="c.id">
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
              {{ c.status === "ACTIVE" ? "Hoạt động" : "Đã ẩn" }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-outline-primary me-1" @click="openEdit(c)">
              <i class="bi bi-pencil"></i>
            </button>
            <button v-if="isAdmin" class="btn btn-sm btn-outline-danger" @click="remove(c)" title="Xóa danh mục (chỉ admin)">
              <i class="bi bi-eye-slash"></i>
            </button>
          </td>
        </tr>
        <tr v-if="pagedItems.length === 0">
          <td colspan="6" class="text-center text-muted py-4">Chưa có danh mục nào.</td>
        </tr>
      </tbody>
    </table>

    <AppPagination v-model="page" :total-items="items.length" :per-page="perPage" />

    <!-- Modal -->
    <div v-if="showModal" class="modal-backdrop-custom" @click.self="showModal = false">
      <div class="modal-box">
        <h5 class="fw-bold mb-3">{{ editingId ? "Sửa danh mục" : "Thêm danh mục" }}</h5>
        <div class="mb-3">
          <label class="form-label">Tên danh mục</label>
          <input
            v-model.trim="form.name"
            type="text"
            class="form-control"
            :class="{ 'is-invalid': error }"
          />
          <div v-if="error" class="field-error">{{ error }}</div>
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
            <option value="INACTIVE">Ẩn</option>
          </select>
        </div>
        <div class="d-flex justify-content-end gap-2">
          <button class="btn btn-secondary" @click="showModal = false">Hủy</button>
          <button class="btn btn-dark" :disabled="saving" @click="save">
            {{ saving ? "Đang lưu..." : "Lưu" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCategories, createCategory, updateCategory, deleteCategory } from "@/api/category";
import { toast } from "@/utils/toast";
import AppPagination from "@/components/Pagination.vue";
import { getUser } from "@/utils/session";

export default {
  name: "AdminCategory",
  components: { AppPagination },
  data() {
    return {
      items: [],
      showModal: false,
      editingId: null,
      form: { name: "", description: "", img: "", status: "ACTIVE" },
      error: "",
      saving: false,
      page: 1,
      perPage: 8,
    };
  },
  computed: {
    isAdmin() {
      return (getUser()?.role || "").toLowerCase() === "admin";
    },
    pagedItems() {
      const start = (this.page - 1) * this.perPage;
      return this.items.slice(start, start + this.perPage);
    },
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
      this.error = "";
      this.showModal = true;
    },
    openEdit(item) {
      this.editingId = item.id;
      this.form = { ...item };
      this.error = "";
      this.showModal = true;
    },
    async save() {
      if (!this.form.name || !this.form.name.trim()) {
        this.error = "Vui lòng nhập tên danh mục!";
        return;
      }
      this.error = "";
      this.saving = true;
      try {
        if (this.editingId) {
          await updateCategory(this.editingId, this.form);
          toast.success("Cập nhật danh mục thành công!");
        } else {
          await createCategory(this.form);
          toast.success("Thêm danh mục thành công!");
        }
        this.showModal = false;
        await this.loadItems();
      } catch (e) {
        this.error = e.response?.data?.message || "";
        toast.error(e.response?.data?.message || "Có lỗi xảy ra, vui lòng thử lại!");
      } finally {
        this.saving = false;
      }
    },
    async remove(item) {
      if (!confirm(`Ẩn danh mục "${item.name}"? Danh mục sẽ không hiển thị cho khách hàng.`)) return;
      await deleteCategory(item.id);
      toast.success("Đã ẩn danh mục!");
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
