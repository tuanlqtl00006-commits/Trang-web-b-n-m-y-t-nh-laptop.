<template>
  <div>
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold mb-0">Quản lý tài khoản nhân viên</h4>
      <button class="btn btn-dark" @click="openCreate">
        <i class="bi bi-plus-lg"></i> Thêm nhân viên
      </button>
    </div>

    <table class="table align-middle bg-white">
      <thead>
        <tr>
          <th>ID</th>
          <th>Họ tên</th>
          <th>Email</th>
          <th>Điện thoại</th>
          <th>Trạng thái</th>
          <th style="width: 150px"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="u in pagedItems" :key="u.id">
          <td>{{ u.id }}</td>
          <td>{{ u.fullname }}</td>
          <td>{{ u.email }}</td>
          <td>{{ u.phone }}</td>
          <td>
            <span class="badge" :class="u.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'">
              {{ u.status === "ACTIVE" ? "Hoạt động" : "Đã khóa" }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-outline-secondary me-1" title="Xem chi tiết" @click="openView(u)">
              <i class="bi bi-eye"></i>
            </button>
            <button
              v-if="u.status === 'ACTIVE'"
              class="btn btn-sm btn-outline-danger"
              title="Khóa tài khoản"
              @click="toggleStatus(u, 'INACTIVE')"
            >
              <i class="bi bi-lock"></i>
            </button>
            <button
              v-else
              class="btn btn-sm btn-outline-success"
              title="Kích hoạt lại"
              @click="toggleStatus(u, 'ACTIVE')"
            >
              <i class="bi bi-unlock"></i>
            </button>
          </td>
        </tr>
        <tr v-if="pagedItems.length === 0">
          <td colspan="6" class="text-center text-muted py-4">Chưa có nhân viên nào.</td>
        </tr>
      </tbody>
    </table>

    <AppPagination v-model="page" :total-items="items.length" :per-page="perPage" />

    <!-- Modal xem chi tiết -->
    <div v-if="viewing" class="modal-backdrop-custom" @click.self="viewing = null">
      <div class="modal-box" style="width: 420px">
        <h5 class="fw-bold mb-3">Chi tiết nhân viên</h5>
        <div class="detail-row"><span>ID</span><b>{{ viewing.id }}</b></div>
        <div class="detail-row"><span>Họ tên</span><b>{{ viewing.fullname }}</b></div>
        <div class="detail-row"><span>Email</span><b>{{ viewing.email }}</b></div>
        <div class="detail-row"><span>Điện thoại</span><b>{{ viewing.phone || "—" }}</b></div>
        <div class="detail-row">
          <span>Trạng thái</span>
          <span class="badge" :class="viewing.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'">
            {{ viewing.status === "ACTIVE" ? "Hoạt động" : "Đã khóa" }}
          </span>
        </div>
        <div class="d-flex justify-content-end mt-3">
          <button class="btn btn-secondary" @click="viewing = null">Đóng</button>
        </div>
      </div>
    </div>

    <!-- Modal thêm nhân viên -->
    <div v-if="showModal" class="modal-backdrop-custom" @click.self="closeModal">
      <div class="modal-box" style="width: 460px">
        <h5 class="fw-bold mb-3">Thêm nhân viên</h5>

        <div class="mb-3">
          <label class="form-label">Họ tên</label>
          <input
            v-model.trim="form.fullname"
            type="text"
            class="form-control"
            :class="{ 'is-invalid': errors.fullname }"
          />
          <div v-if="errors.fullname" class="field-error">{{ errors.fullname }}</div>
        </div>
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input
            v-model.trim="form.email"
            type="email"
            class="form-control"
            placeholder="ten@example.com"
            :class="{ 'is-invalid': errors.email }"
          />
          <div v-if="errors.email" class="field-error">{{ errors.email }}</div>
        </div>
        <div class="mb-3">
          <label class="form-label">Số điện thoại</label>
          <input
            v-model.trim="form.phone"
            type="text"
            class="form-control"
            maxlength="10"
            placeholder="VD: 0912345678"
            :class="{ 'is-invalid': errors.phone }"
          />
          <div v-if="errors.phone" class="field-error">{{ errors.phone }}</div>
        </div>
        <div class="mb-3">
          <label class="form-label">Mật khẩu</label>
          <input
            v-model="form.password"
            type="password"
            class="form-control"
            :class="{ 'is-invalid': errors.password }"
          />
          <div v-if="errors.password" class="field-error">{{ errors.password }}</div>
        </div>

        <div class="d-flex justify-content-end gap-2 mt-3">
          <button class="btn btn-secondary" @click="closeModal">Hủy</button>
          <button class="btn btn-dark" :disabled="saving" @click="save">
            {{ saving ? "Đang thêm..." : "Thêm nhân viên" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getUsers, createUser, setUserStatus } from "@/api/user";
import { emailError, phoneError } from "@/utils/validate";
import { toast } from "@/utils/toast";
import AppPagination from "@/components/Pagination.vue";

const emptyForm = () => ({
  fullname: "",
  email: "",
  phone: "",
  password: "",
  role: "STAFF",
});

export default {
  name: "AdminStaff",
  components: { AppPagination },
  data() {
    return {
      allItems: [],
      showModal: false,
      viewing: null,
      saving: false,
      form: emptyForm(),
      errors: {},
      page: 1,
      perPage: 10,
    };
  },
  computed: {
    items() {
      return this.allItems.filter((u) => (u.role || "").toUpperCase() === "STAFF");
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
      const res = await getUsers();
      this.allItems = res.data;
    },
    openView(user) {
      this.viewing = user;
    },
    openCreate() {
      this.form = emptyForm();
      this.errors = {};
      this.showModal = true;
    },
    closeModal() {
      this.showModal = false;
      this.form = emptyForm();
      this.errors = {};
    },
    validate() {
      this.errors = {};
      if (!this.form.fullname || !this.form.fullname.trim()) this.errors.fullname = "Vui lòng nhập họ tên!";
      const emailErr = emailError(this.form.email);
      if (emailErr) this.errors.email = emailErr;
      const phoneErr = phoneError(this.form.phone);
      if (phoneErr) this.errors.phone = phoneErr;
      if (!this.form.password || this.form.password.length < 4)
        this.errors.password = "Mật khẩu cần tối thiểu 4 ký tự!";
      return Object.keys(this.errors).length === 0;
    },
    async save() {
      if (!this.validate()) return;
      this.saving = true;
      try {
        await createUser({ ...this.form, role: "STAFF" });
        toast.success("Thêm nhân viên thành công!");
        this.closeModal();
        await this.loadItems();
      } catch (e) {
        const msg = e.response?.data?.message || "Có lỗi xảy ra, vui lòng thử lại!";
        toast.error(msg);
        if (msg.toLowerCase().includes("email")) this.errors.email = msg;
      } finally {
        this.saving = false;
      }
    },
    async toggleStatus(user, status) {
      const label = status === "ACTIVE" ? "kích hoạt lại" : "khóa";
      if (!confirm(`Bạn có chắc muốn ${label} tài khoản "${user.fullname}"?`)) return;
      await setUserStatus(user.id, status);
      user.status = status;
      toast.success(status === "ACTIVE" ? "Đã kích hoạt lại tài khoản!" : "Đã khóa tài khoản!");
    },
  },
};
</script>

<style scoped>
.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 0.4rem 0;
  border-bottom: 1px solid #f0f0f0;
}
.detail-row span {
  color: #6c757d;
}
</style>
