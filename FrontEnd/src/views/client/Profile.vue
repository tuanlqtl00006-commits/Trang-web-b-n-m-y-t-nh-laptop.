<template>
  <div>
    <h4 class="fw-bold mb-4"><i class="bi bi-person-circle me-2"></i>Thông tin tài khoản</h4>

    <div v-if="successMsg" class="alert alert-success">{{ successMsg }}</div>
    <div v-if="errorMsg" class="alert alert-danger">{{ errorMsg }}</div>

    <div class="card p-4" style="max-width: 500px">
      <div class="mb-3">
        <label class="form-label">Họ và tên</label>
        <input v-model="form.fullname" type="text" class="form-control" />
      </div>
      <div class="mb-3">
        <label class="form-label">Email</label>
        <input v-model="form.email" type="email" class="form-control" disabled />
      </div>
      <div class="mb-3">
        <label class="form-label">Số điện thoại</label>
        <input v-model="form.phone" type="text" class="form-control" />
      </div>
      <div class="mb-3">
        <label class="form-label">Mật khẩu mới (để trống nếu không đổi)</label>
        <input v-model="form.password" type="password" class="form-control" />
      </div>
      <button class="btn btn-dark" :disabled="saving" @click="saveProfile">
        {{ saving ? "Đang lưu..." : "Lưu thay đổi" }}
      </button>
    </div>
  </div>
</template>

<script>
import { updateUser } from "@/api/user";

export default {
  name: "Profile",
  data() {
    return {
      form: { fullname: "", email: "", phone: "", password: "" },
      saving: false,
      successMsg: "",
      errorMsg: "",
    };
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem("user") || "null");
    if (!user) {
      this.$router.push("/login");
      return;
    }
    this.form.fullname = user.fullname;
    this.form.email = user.email;
    this.form.phone = user.phone;
  },
  methods: {
    async saveProfile() {
      this.successMsg = "";
      this.errorMsg = "";
      this.saving = true;
      const user = JSON.parse(localStorage.getItem("user") || "null");
      try {
        const payload = {
          fullname: this.form.fullname,
          email: this.form.email,
          phone: this.form.phone,
          role: user.role,
          status: user.status,
        };
        if (this.form.password) payload.password = this.form.password;

        const res = await updateUser(user.id, payload);
        const updatedUser = { ...user, ...res.data };
        localStorage.setItem("user", JSON.stringify(updatedUser));
        window.dispatchEvent(new Event("user-logged-in"));
        this.successMsg = "Cập nhật thông tin thành công!";
        this.form.password = "";
      } catch (e) {
        this.errorMsg = "Cập nhật thất bại, vui lòng thử lại!";
      } finally {
        this.saving = false;
      }
    },
  },
};
</script>
