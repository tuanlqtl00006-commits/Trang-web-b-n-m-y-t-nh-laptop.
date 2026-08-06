<template>
  <div class="d-flex justify-content-center align-items-center auth-wrapper">
    <div class="card shadow-sm p-4" style="width: 400px">
      <h4 class="text-center fw-bold mb-4">
        <i class="bi bi-cpu me-1"></i>Đăng ký tài khoản
      </h4>

      <div v-if="errorMsg" class="alert alert-danger py-2">{{ errorMsg }}</div>
      <div v-if="successMsg" class="alert alert-success py-2">{{ successMsg }}</div>

      <form @submit.prevent="handleRegister" novalidate>
        <div class="mb-3">
          <label class="form-label">Họ và tên</label>
          <input
            v-model.trim="form.fullname"
            type="text"
            class="form-control"
            :class="{ 'is-invalid': fieldErrors.fullname }"
          />
          <div v-if="fieldErrors.fullname" class="field-error">{{ fieldErrors.fullname }}</div>
        </div>
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input
            v-model.trim="form.email"
            type="email"
            class="form-control"
            placeholder="ten@example.com"
            :class="{ 'is-invalid': fieldErrors.email }"
          />
          <div v-if="fieldErrors.email" class="field-error">{{ fieldErrors.email }}</div>
        </div>
        <div class="mb-3">
          <label class="form-label">Số điện thoại</label>
          <input
            v-model.trim="form.phone"
            type="text"
            class="form-control"
            placeholder="VD: 0912345678"
            maxlength="10"
            :class="{ 'is-invalid': fieldErrors.phone }"
          />
          <div v-if="fieldErrors.phone" class="field-error">{{ fieldErrors.phone }}</div>
        </div>
        <div class="mb-3">
          <label class="form-label">Mật khẩu</label>
          <input
            v-model="form.password"
            type="password"
            class="form-control"
            :class="{ 'is-invalid': fieldErrors.password }"
          />
          <div v-if="fieldErrors.password" class="field-error">{{ fieldErrors.password }}</div>
        </div>
        <button type="submit" class="btn btn-dark w-100" :disabled="loading">
          {{ loading ? "Đang đăng ký..." : "Đăng ký" }}
        </button>
      </form>

      <div class="text-center mt-3 small">
        Đã có tài khoản?
        <router-link to="/login">Đăng nhập</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { registerAPI } from "@/api/auth";
import { emailError, phoneError } from "@/utils/validate";
import { toast } from "@/utils/toast";

export default {
  name: "Register",
  data() {
    return {
      form: { fullname: "", email: "", phone: "", password: "" },
      fieldErrors: { fullname: "", email: "", phone: "", password: "" },
      errorMsg: "",
      successMsg: "",
      loading: false,
    };
  },
  methods: {
    validate() {
      this.fieldErrors.fullname = this.form.fullname ? "" : "Vui lòng nhập họ tên!";
      this.fieldErrors.email = emailError(this.form.email);
      this.fieldErrors.phone = phoneError(this.form.phone);
      this.fieldErrors.password =
        this.form.password && this.form.password.length >= 4
          ? ""
          : "Mật khẩu cần tối thiểu 4 ký tự!";
      return !Object.values(this.fieldErrors).some(Boolean);
    },
    async handleRegister() {
      this.errorMsg = "";
      this.successMsg = "";
      if (!this.validate()) return;

      this.loading = true;
      try {
        await registerAPI(this.form);
        this.successMsg = "Đăng ký thành công! Đang chuyển sang đăng nhập...";
        toast.success("Đăng ký tài khoản thành công!");
        setTimeout(() => this.$router.push("/login"), 1200);
      } catch (err) {
        this.errorMsg = err.response?.data?.message || "Email đã tồn tại!";
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.auth-wrapper {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e, #16213e);
}
</style>
