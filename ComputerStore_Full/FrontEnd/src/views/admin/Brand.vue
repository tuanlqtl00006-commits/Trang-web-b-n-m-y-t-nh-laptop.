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
        <tr v-for="item in pagedItems" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.brandName }}</td>
          <td>
            <span class="badge" :class="item.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'">
              {{ item.status === "ACTIVE" ? "Hoạt động" : "Đã ẩn" }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-outline-primary me-1" @click="openEdit(item)">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn btn-sm btn-outline-danger" @click="remove(item)">
              <i class="bi bi-eye-slash"></i>
            </button>
          </td>
        </tr>
        <tr v-if="pagedItems.length === 0">
          <td colspan="4" class="text-center text-muted py-4">Chưa có dữ liệu.</td>
        </tr>
      </tbody>
    </table>

    <AppPagination v-model="page" :total-items="items.length" :per-page="perPage" />

    <div v-if="showModal" class="modal-backdrop-custom" @click.self="showModal = false">
      <div class="modal-box">
        <h5 class="fw-bold mb-3">{{ editingId ? "Sửa Tên thương hiệu" : "Thêm thương hiệu" }}</h5>
        <div class="mb-3">
          <label class="form-label">Tên thương hiệu</label>
          <input
            v-model.trim="form.brandName"
            type="text"
            class="form-control"
            placeholder="VD: Dell, Asus, Lenovo"
            :class="{ 'is-invalid': error }"
          />
          <div v-if="error" class="field-error">{{ error }}</div>
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
import { getBrands, createBrand, updateBrand, deleteBrand } from "@/api/brand";
import { toast } from "@/utils/toast";
import AppPagination from "@/components/Pagination.vue";

export default {
  name: "AdminBrand",
  components: { AppPagination },
  data() {
    return {
      items: [],
      showModal: false,
      editingId: null,
      form: { brandName: "", status: "ACTIVE" },
      error: "",
      saving: false,
      page: 1,
      perPage: 8,
    };
  },
  computed: {
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
      const res = await getBrands();
      this.items = res.data;
    },
    openCreate() {
      this.editingId = null;
      this.form = { brandName: "", status: "ACTIVE" };
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
      if (!this.form.brandName || !this.form.brandName.trim()) {
        this.error = "Vui lòng nhập tên thương hiệu!";
        return;
      }
      this.error = "";
      this.saving = true;
      try {
        if (this.editingId) {
          await updateBrand(this.editingId, this.form);
          toast.success("Cập nhật thành công!");
        } else {
          await createBrand(this.form);
          toast.success("Thêm thành công!");
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
      if (!confirm(`Ẩn thương hiệu "${item.brandName}"?`)) return;
      await deleteBrand(item.id);
      toast.success("Đã ẩn!");
      await this.loadItems();
    },
  },
};
</script>
