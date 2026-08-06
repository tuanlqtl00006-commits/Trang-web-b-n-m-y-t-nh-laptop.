<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold mb-0">Quản lý sản phẩm</h4>
      <button class="btn btn-dark" @click="openCreate">
        <i class="bi bi-plus-lg"></i> Thêm sản phẩm
      </button>
    </div>

    <table class="table align-middle bg-white">
      <thead>
        <tr>
          <th>Ảnh</th>
          <th>Tên sản phẩm</th>
          <th>Giá</th>
          <th>Kho</th>
          <th>Danh mục</th>
          <th>Thương hiệu</th>
          <th>Trạng thái</th>
          <th style="width: 140px"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in items" :key="p.id">
          <td><img :src="productImage(p)" width="50" height="50" style="object-fit: contain" /></td>
          <td>{{ p.name }}</td>
          <td>{{ formatPrice(p.price) }}₫</td>
          <td>{{ p.stock }}</td>
          <td>{{ categoryName(p.categoryId) }}</td>
          <td>{{ brandName(p.brandId) }}</td>
          <td>
            <span class="badge" :class="p.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'">
              {{ p.status === "ACTIVE" ? "Hoạt động" : "Ngừng" }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-outline-primary me-1" @click="openEdit(p)">
              <i class="bi bi-pencil"></i>
            </button>
            <button class="btn btn-sm btn-outline-danger" @click="remove(p.id)">
              <i class="bi bi-trash"></i>
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Modal thêm/sửa -->
    <div v-if="showModal" class="modal-backdrop-custom" @click.self="showModal = false">
      <div class="modal-box">
        <h5 class="fw-bold mb-3">{{ editingId ? "Sửa sản phẩm" : "Thêm sản phẩm" }}</h5>

        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label">Tên sản phẩm</label>
            <input v-model="form.name" type="text" class="form-control" />
          </div>
          <div class="col-md-6">
            <label class="form-label">Giá bán</label>
            <input v-model.number="form.price" type="number" class="form-control" />
          </div>

          <div class="col-md-6">
            <label class="form-label">Danh mục</label>
            <select v-model.number="form.categoryId" class="form-select">
              <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
            </select>
          </div>
          <div class="col-md-6">
            <label class="form-label">Thương hiệu</label>
            <select v-model.number="form.brandId" class="form-select">
              <option v-for="b in brands" :key="b.id" :value="b.id">{{ b.brandName }}</option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">CPU</label>
            <select v-model.number="form.cpuId" class="form-select">
              <option v-for="c in cpus" :key="c.id" :value="c.id">{{ c.cpuName }}</option>
            </select>
          </div>
          <div class="col-md-6">
            <label class="form-label">RAM</label>
            <select v-model.number="form.ramId" class="form-select">
              <option v-for="r in rams" :key="r.id" :value="r.id">{{ r.ramName }}</option>
            </select>
          </div>

          <div class="col-md-4">
            <label class="form-label">SSD</label>
            <input v-model="form.ssd" type="text" class="form-control" placeholder="512GB SSD" />
          </div>
          <div class="col-md-4">
            <label class="form-label">GPU</label>
            <input v-model="form.gpu" type="text" class="form-control" placeholder="RTX 4060" />
          </div>
          <div class="col-md-4">
            <label class="form-label">Màn hình</label>
            <input v-model="form.screen" type="text" class="form-control" placeholder="15.6 inch FHD" />
          </div>

          <div class="col-md-6">
            <label class="form-label">Hệ điều hành</label>
            <input v-model="form.os" type="text" class="form-control" placeholder="Windows 11" />
          </div>
          <div class="col-md-6">
            <label class="form-label">Tồn kho</label>
            <input v-model.number="form.stock" type="number" class="form-control" />
          </div>

          <div class="col-md-6">
            <label class="form-label">Trạng thái</label>
            <select v-model="form.status" class="form-select">
              <option value="ACTIVE">Hoạt động</option>
              <option value="INACTIVE">Ngừng</option>
            </select>
          </div>
          <div class="col-md-6">
            <label class="form-label">Ảnh sản phẩm {{ editingId ? "(để trống nếu giữ ảnh cũ)" : "" }}</label>
            <input type="file" class="form-control" accept="image/*" @change="onFileChange" />
          </div>

          <div class="col-12">
            <label class="form-label">Mô tả</label>
            <textarea v-model="form.description" class="form-control" rows="3"></textarea>
          </div>
        </div>

        <div class="d-flex justify-content-end gap-2 mt-3">
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
import {
  getProducts,
  updateProduct,
  deleteProduct,
  createProductWithImage,
} from "@/api/product";
import { getCategories } from "@/api/category";
import { getBrands } from "@/api/brand";
import { getCpus } from "@/api/cpu";
import { getRams } from "@/api/ram";

const emptyForm = () => ({
  name: "",
  price: 0,
  stock: 0,
  status: "ACTIVE",
  description: "",
  ssd: "",
  gpu: "",
  screen: "",
  os: "",
  categoryId: null,
  brandId: null,
  cpuId: null,
  ramId: null,
});

export default {
  name: "AdminProducts",
  data() {
    return {
      items: [],
      categories: [],
      brands: [],
      cpus: [],
      rams: [],
      showModal: false,
      editingId: null,
      saving: false,
      form: emptyForm(),
      selectedFile: null,
    };
  },
  async mounted() {
    await this.loadAll();
  },
  methods: {
    async loadAll() {
      const [pRes, cRes, bRes, cpuRes, ramRes] = await Promise.all([
        getProducts(),
        getCategories(),
        getBrands(),
        getCpus(),
        getRams(),
      ]);
      this.items = pRes.data;
      this.categories = cRes.data;
      this.brands = bRes.data;
      this.cpus = cpuRes.data;
      this.rams = ramRes.data;
    },
    categoryName(id) {
      return this.categories.find((c) => c.id === id)?.name || "—";
    },
    brandName(id) {
      return this.brands.find((b) => b.id === id)?.brandName || "—";
    },
    openCreate() {
      this.editingId = null;
      this.form = emptyForm();
      this.selectedFile = null;
      this.showModal = true;
    },
    openEdit(item) {
      this.editingId = item.id;
      this.form = { ...item };
      this.selectedFile = null;
      this.showModal = true;
    },
    onFileChange(e) {
      this.selectedFile = e.target.files[0] || null;
    },
    async save() {
      if (!this.form.name || !this.form.categoryId || !this.form.brandId || !this.form.cpuId || !this.form.ramId) {
        alert("Vui lòng nhập đầy đủ thông tin bắt buộc (tên, danh mục, thương hiệu, CPU, RAM)!");
        return;
      }

      this.saving = true;
      try {
        if (this.editingId) {
          // Cập nhật: dùng API JSON thường (giữ ảnh cũ nếu không chọn file mới)
          await updateProduct(this.editingId, {
            ...this.form,
            image: this.form.image,
          });
        } else {
          // Tạo mới: bắt buộc phải có ảnh -> dùng API upload
          if (!this.selectedFile) {
            alert("Vui lòng chọn ảnh sản phẩm!");
            this.saving = false;
            return;
          }
          const fd = new FormData();
          fd.append("name", this.form.name);
          fd.append("price", this.form.price);
          fd.append("categoryId", this.form.categoryId);
          fd.append("brandId", this.form.brandId);
          fd.append("cpuId", this.form.cpuId);
          fd.append("ramId", this.form.ramId);
          fd.append("stock", this.form.stock);
          fd.append("status", this.form.status);
          fd.append("description", this.form.description || "");
          fd.append("ssd", this.form.ssd || "");
          fd.append("gpu", this.form.gpu || "");
          fd.append("screen", this.form.screen || "");
          fd.append("os", this.form.os || "");
          fd.append("file", this.selectedFile);
          await createProductWithImage(fd);
        }
        this.showModal = false;
        await this.loadAll();
      } catch (e) {
        console.error(e);
        alert("Có lỗi xảy ra khi lưu sản phẩm!");
      } finally {
        this.saving = false;
      }
    },
    async remove(id) {
      if (!confirm("Bạn có chắc muốn xóa sản phẩm này?")) return;
      await deleteProduct(id);
      await this.loadAll();
    },
    formatPrice(price) {
      if (!price) return "0";
      return price.toLocaleString("vi-VN");
    },
    productImage(p) {
      if (p.image) return encodeURI(p.image);
      return "";
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
  overflow-y: auto;
  padding: 20px 0;
}
.modal-box {
  background: white;
  border-radius: 8px;
  padding: 24px;
  width: 720px;
  max-width: 95vw;
  max-height: 90vh;
  overflow-y: auto;
}
</style>
