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
        <tr v-for="p in pagedItems" :key="p.id">
          <td><img :src="productImage(p)" width="50" height="50" style="object-fit: contain" /></td>
          <td>{{ p.name }}</td>
          <td>{{ formatPrice(p.price) }}₫</td>
          <td>
            {{ p.stock }}
            <span v-if="p.stock <= LOW_STOCK_THRESHOLD" class="low-stock-hint d-block">
              <i class="bi bi-exclamation-triangle"></i> Sắp hết hàng
            </span>
          </td>
          <td>{{ categoryName(p.categoryId) }}</td>
          <td>{{ brandName(p.brandId) }}</td>
          <td>
            <span class="badge" :class="p.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'">
              {{ p.status === "ACTIVE" ? "Hoạt động" : "Đã ẩn" }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-outline-primary me-1" @click="openEdit(p)">
              <i class="bi bi-pencil"></i>
            </button>
            <button v-if="isAdmin" class="btn btn-sm btn-outline-danger" @click="openDeleteChoice(p)" title="Xóa sản phẩm (chỉ admin)">
              <i class="bi bi-trash"></i>
            </button>
          </td>
        </tr>
        <tr v-if="pagedItems.length === 0">
          <td colspan="8" class="text-center text-muted py-4">Chưa có sản phẩm nào.</td>
        </tr>
      </tbody>
    </table>

    <AppPagination v-model="page" :total-items="items.length" :per-page="perPage" />

    <!-- Modal thêm/sửa -->
    <div v-if="showModal" class="modal-backdrop-custom" @click.self="closeModal">
      <div class="modal-box">
        <h5 class="fw-bold mb-3">{{ editingId ? "Sửa sản phẩm" : "Thêm sản phẩm" }}</h5>

        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label">Tên sản phẩm</label>
            <input v-model="form.name" type="text" class="form-control" :class="{ 'is-invalid': errors.name }" />
            <div v-if="errors.name" class="field-error">{{ errors.name }}</div>
          </div>
          <div class="col-md-6">
            <label class="form-label">Giá bán (₫) — từ {{ MIN_PRICE.toLocaleString("vi-VN") }} đến {{ MAX_PRICE.toLocaleString("vi-VN") }}</label>
            <input
              v-model.number="form.price"
              type="number"
              :min="MIN_PRICE"
              :max="MAX_PRICE"
              step="100000"
              class="form-control"
              :class="{ 'is-invalid': errors.price }"
            />
            <div v-if="errors.price" class="field-error">{{ errors.price }}</div>
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

          <div class="col-12">
            <label class="form-label">SSD / Ổ cứng</label>
            <div class="radio-chip-group">
              <label class="radio-chip" v-for="opt in ssdOptions" :key="opt">
                <input type="radio" name="ssdOpt" :value="opt" v-model="ssdChoice" />
                <span>{{ opt }}</span>
              </label>
              <label class="radio-chip">
                <input type="radio" name="ssdOpt" value="__other__" v-model="ssdChoice" />
                <span>Khác</span>
              </label>
            </div>
            <input
              v-if="ssdChoice === '__other__'"
              v-model="form.ssd"
              type="text"
              class="form-control mt-2"
              placeholder="Nhập cấu hình SSD khác..."
            />
          </div>

          <div class="col-12">
            <label class="form-label">GPU</label>
            <div class="radio-chip-group">
              <label class="radio-chip" v-for="opt in gpuOptions" :key="opt">
                <input type="radio" name="gpuOpt" :value="opt" v-model="gpuChoice" />
                <span>{{ opt }}</span>
              </label>
              <label class="radio-chip">
                <input type="radio" name="gpuOpt" value="__other__" v-model="gpuChoice" />
                <span>Khác</span>
              </label>
            </div>
            <input
              v-if="gpuChoice === '__other__'"
              v-model="form.gpu"
              type="text"
              class="form-control mt-2"
              placeholder="Nhập GPU khác..."
            />
          </div>

          <div class="col-12">
            <label class="form-label">Màn hình</label>
            <div class="radio-chip-group">
              <label class="radio-chip" v-for="opt in screenOptions" :key="opt">
                <input type="radio" name="screenOpt" :value="opt" v-model="screenChoice" />
                <span>{{ opt }}</span>
              </label>
              <label class="radio-chip">
                <input type="radio" name="screenOpt" value="__other__" v-model="screenChoice" />
                <span>Khác</span>
              </label>
            </div>
            <input
              v-if="screenChoice === '__other__'"
              v-model="form.screen"
              type="text"
              class="form-control mt-2"
              placeholder="Nhập màn hình khác..."
            />
          </div>

          <div class="col-md-6">
            <label class="form-label">Hệ điều hành</label>
            <div class="radio-chip-group">
              <label class="radio-chip" v-for="opt in osOptions" :key="opt">
                <input type="radio" name="osOpt" :value="opt" v-model="form.os" />
                <span>{{ opt }}</span>
              </label>
            </div>
            <div v-if="errors.os" class="field-error">{{ errors.os }}</div>
          </div>
          <div class="col-md-6">
            <label class="form-label">Tồn kho (tối đa {{ MAX_STOCK.toLocaleString("vi-VN") }})</label>
            <input
              v-model.number="form.stock"
              type="number"
              min="0"
              :max="MAX_STOCK"
              class="form-control"
              :class="{ 'is-invalid': errors.stock }"
            />
            <div v-if="errors.stock" class="field-error">{{ errors.stock }}</div>
          </div>

          <div class="col-md-6">
            <label class="form-label">Trạng thái</label>
            <select v-model="form.status" class="form-select">
              <option value="ACTIVE">Hoạt động</option>
              <option value="INACTIVE">Ẩn</option>
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
          <button class="btn btn-secondary" @click="closeModal">Hủy</button>
          <button class="btn btn-dark" :disabled="saving" @click="save">
            {{ saving ? "Đang lưu..." : "Lưu" }}
          </button>
        </div>
      </div>
    </div>

    <!-- Modal chọn cách xóa: ẩn / xóa hẳn -->
    <div v-if="deleteTarget" class="modal-backdrop-custom" @click.self="deleteTarget = null">
      <div class="modal-box choice-modal" style="width: 460px">
        <h5 class="fw-bold mb-1">Xóa sản phẩm</h5>
        <p class="text-muted small mb-3">"{{ deleteTarget.name }}" — chọn cách xử lý:</p>

        <button class="choice-option" @click="confirmDelete('hide')">
          <i class="bi bi-eye-slash text-primary"></i>
          <div>
            <div class="choice-title">1. Ẩn sản phẩm</div>
            <div class="choice-desc">Ngừng hiển thị cho khách hàng nhưng vẫn giữ lịch sử đơn hàng. Có thể bật lại sau.</div>
          </div>
        </button>

        <button class="choice-option danger" @click="confirmDelete('hard')">
          <i class="bi bi-trash3 text-danger"></i>
          <div>
            <div class="choice-title">2. Xóa hẳn</div>
            <div class="choice-desc">Xóa vĩnh viễn khỏi hệ thống, không thể khôi phục. Không áp dụng được nếu sản phẩm đã có trong đơn hàng.</div>
          </div>
        </button>

        <div class="text-end mt-2">
          <button class="btn btn-secondary btn-sm" @click="deleteTarget = null">Hủy</button>
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
import { toast } from "@/utils/toast";
import AppPagination from "@/components/Pagination.vue";
import { getUser } from "@/utils/session";

const SSD_OPTIONS = ["256GB SSD", "512GB SSD", "1TB SSD", "512GB NVMe", "1TB NVMe", "2TB SSD"];
const GPU_OPTIONS = [
  "Intel UHD/Iris (onboard)",
  "AMD Radeon (onboard)",
  "NVIDIA RTX 4050",
  "NVIDIA RTX 4060",
  "NVIDIA RTX 4070",
  "NVIDIA RTX 4080",
  "NVIDIA RTX 4090",
  "Apple GPU (M-series)",
];
const SCREEN_OPTIONS = [
  "14 inch FHD",
  "15.6 inch FHD",
  "16 inch FHD",
  "17 inch FHD",
  "15.6 inch QHD",
  "14 inch OLED",
];

const MAX_STOCK = 200; // cửa hàng nhỏ, không nhập tồn kho quá nhiều
const LOW_STOCK_THRESHOLD = 5;
const MIN_PRICE = 9_000_000;
const MAX_PRICE = 300_000_000;
const OS_OPTIONS = ["Windows 10", "Windows 11"];

const emptyForm = () => ({
  name: "",
  price: MIN_PRICE,
  stock: 0,
  status: "ACTIVE",
  description: "",
  ssd: "",
  gpu: "",
  screen: "",
  os: "Windows 11",
  categoryId: null,
  brandId: null,
  cpuId: null,
  ramId: null,
});

export default {
  name: "AdminProducts",
  components: { AppPagination },
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
      errors: {},
      deleteTarget: null,
      page: 1,
      perPage: 8,
      ssdOptions: SSD_OPTIONS,
      gpuOptions: GPU_OPTIONS,
      screenOptions: SCREEN_OPTIONS,
      ssdChoice: "",
      gpuChoice: "",
      screenChoice: "",
      MAX_STOCK,
      LOW_STOCK_THRESHOLD,
      MIN_PRICE,
      MAX_PRICE,
      osOptions: OS_OPTIONS,
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
  watch: {
    ssdChoice(v) {
      if (v !== "__other__") this.form.ssd = v;
    },
    gpuChoice(v) {
      if (v !== "__other__") this.form.gpu = v;
    },
    screenChoice(v) {
      if (v !== "__other__") this.form.screen = v;
    },
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
      if (this.page > Math.ceil(this.items.length / this.perPage)) this.page = 1;
    },
    categoryName(id) {
      return this.categories.find((c) => c.id === id)?.name || "—";
    },
    brandName(id) {
      return this.brands.find((b) => b.id === id)?.brandName || "—";
    },
    choiceFor(options, value) {
      return value && options.includes(value) ? value : value ? "__other__" : "";
    },
    openCreate() {
      this.editingId = null;
      this.form = emptyForm();
      this.selectedFile = null;
      this.errors = {};
      this.ssdChoice = "";
      this.gpuChoice = "";
      this.screenChoice = "";
      this.showModal = true;
    },
    openEdit(item) {
      this.editingId = item.id;
      this.form = { ...item };
      this.selectedFile = null;
      this.errors = {};
      this.ssdChoice = this.choiceFor(SSD_OPTIONS, item.ssd);
      this.gpuChoice = this.choiceFor(GPU_OPTIONS, item.gpu);
      this.screenChoice = this.choiceFor(SCREEN_OPTIONS, item.screen);
      this.showModal = true;
    },
    closeModal() {
      this.showModal = false;
    },
    onFileChange(e) {
      this.selectedFile = e.target.files[0] || null;
    },
    validate() {
      this.errors = {};
      if (!this.form.name || !this.form.name.trim()) this.errors.name = "Vui lòng nhập tên sản phẩm!";
      if (
        this.form.price === null ||
        this.form.price === undefined ||
        this.form.price < MIN_PRICE ||
        this.form.price > MAX_PRICE
      )
        this.errors.price = `Giá bán phải từ ${MIN_PRICE.toLocaleString("vi-VN")}đ đến ${MAX_PRICE.toLocaleString("vi-VN")}đ!`;
      if (this.form.stock === null || this.form.stock === undefined || this.form.stock < 0)
        this.errors.stock = "Số lượng tồn kho không được âm!";
      else if (this.form.stock > MAX_STOCK)
        this.errors.stock = `Tồn kho vượt quá giới hạn cho phép (${MAX_STOCK.toLocaleString("vi-VN")})!`;
      if (!OS_OPTIONS.includes(this.form.os)) this.errors.os = "Vui lòng chọn hệ điều hành!";
      if (!this.form.categoryId || !this.form.brandId || !this.form.cpuId || !this.form.ramId) {
        toast.error("Vui lòng chọn đầy đủ danh mục, thương hiệu, CPU, RAM!");
        return false;
      }
      return Object.keys(this.errors).length === 0;
    },
    async save() {
      if (!this.validate()) return;

      this.saving = true;
      try {
        if (this.editingId) {
          await updateProduct(this.editingId, { ...this.form });
          toast.success("Cập nhật sản phẩm thành công!");
        } else {
          if (!this.selectedFile) {
            toast.error("Vui lòng chọn ảnh sản phẩm!");
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
          toast.success("Thêm sản phẩm thành công!");
        }
        this.showModal = false;
        await this.loadAll();
      } catch (e) {
        toast.error(e.response?.data?.message || "Có lỗi xảy ra khi lưu sản phẩm!");
      } finally {
        this.saving = false;
      }
    },
    openDeleteChoice(item) {
      this.deleteTarget = item;
    },
    async confirmDelete(mode) {
      const item = this.deleteTarget;
      this.deleteTarget = null;
      try {
        await deleteProduct(item.id, mode);
        toast.success(mode === "hard" ? "Đã xóa vĩnh viễn sản phẩm!" : "Đã ẩn sản phẩm!");
        await this.loadAll();
      } catch (e) {
        toast.error(e.response?.data?.message || "Không thể xóa sản phẩm!");
      }
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
