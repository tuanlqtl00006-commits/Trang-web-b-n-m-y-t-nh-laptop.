<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold mb-0">Quản lý RAM</h4>
      <button class="btn btn-dark" @click="openCreate">
        <i class="bi bi-plus-lg"></i> Thêm RAM
      </button>
    </div>

    <table class="table align-middle bg-white">
      <thead>
        <tr>
          <th>ID</th>
          <th>Tên RAM</th>
          <th>Trạng thái</th>
          <th style="width: 140px"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in items" :key="r.id">
          <td>{{ r.id }}</td>
          <td>{{ r.ramName }}</td>
          <td>
            <span class="badge" :class="r.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'">
              {{ r.status === "ACTIVE" ? "Hoạt động" : "Ngừng" }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-outline-primary me-1" @click="openEdit(r)">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn btn-sm btn-outline-danger" @click="remove(r.id)">
              <i class="bi bi-trash"></i>
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showModal" class="modal-backdrop-custom" @click.self="showModal = false">
      <div class="modal-box">
        <h5 class="fw-bold mb-3">{{ editingId ? "Sửa RAM" : "Thêm RAM" }}</h5>
        <div class="mb-3">
          <label class="form-label">Tên RAM</label>
          <input v-model="form.ramName" type="text" class="form-control" placeholder="VD: 16GB DDR4" />
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
import { getRams, createRam, updateRam, deleteRam } from "@/api/ram";

export default {
  name: "AdminRam",
  data() {
    return {
      items: [],
      showModal: false,
      editingId: null,
      form: { ramName: "", status: "ACTIVE" },
    };
  },
  mounted() {
    this.loadItems();
  },
  methods: {
    async loadItems() {
      const res = await getRams();
      this.items = res.data;
    },
    openCreate() {
      this.editingId = null;
      this.form = { ramName: "", status: "ACTIVE" };
      this.showModal = true;
    },
    openEdit(item) {
      this.editingId = item.id;
      this.form = { ...item };
      this.showModal = true;
    },
    async save() {
      if (!this.form.ramName) {
        alert("Vui lòng nhập tên RAM!");
        return;
      }
      if (this.editingId) {
        await updateRam(this.editingId, this.form);
      } else {
        await createRam(this.form);
      }
      this.showModal = false;
      await this.loadItems();
    },
    async remove(id) {
      if (!confirm("Bạn có chắc muốn xóa RAM này?")) return;
      await deleteRam(id);
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
