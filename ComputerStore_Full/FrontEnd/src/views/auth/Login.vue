<template>
  <div class="d-flex justify-content-center align-items-center auth-wrapper">
    <div class="card shadow-sm p-4" style="width: 380px">
      <h4 class="text-center fw-bold mb-4">
        <i class="bi bi-cpu me-1"></i>Đăng nhập
      </h4>

      <div v-if="errorMsg" class="alert alert-danger py-2">{{ errorMsg }}</div>

      <form @submit.prevent="handleLogin" novalidate>
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input
            v-model.trim="form.email"
            type="email"
            class="form-control"
            :class="{ 'is-invalid': fieldErrors.email }"
          />
          <div v-if="fieldErrors.email" class="field-error">{{ fieldErrors.email }}</div>
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
          {{ loading ? "Đang đăng nhập..." : "Đăng nhập" }}
        </button>
      </form>

      <div class="text-center mt-3 small">
        Chưa có tài khoản?
        <router-link to="/register">Đăng ký ngay</router-link>
      </div>
      <div class="text-center mt-2 small">
        <router-link to="/">← Về trang chủ</router-link>
      </div>

      <div class="demo-accounts mt-4 pt-3 border-top small text-muted">
        <div class="fw-semibold mb-1">Tài khoản demo:</div>
        <div>Admin: admin@computerstore.com / admin123</div>
        <div>Nhân viên: staff@computerstore.com / staff123</div>
        <div>Khách hàng: user1@computerstore.com / user123</div>
      </div>
    </div>
  </div>
</template>

<script>
import { loginAPI } from "@/api/auth";
import { emailError } from "@/utils/validate";
import { toast } from "@/utils/toast";
import { setUser, setToken } from "@/utils/session";

export default {
  name: "Login",
  data() {
    return {
      form: { email: "", password: "" },
      fieldErrors: { email: "", password: "" },
      errorMsg: "",
      loading: false,
    };
  },
  methods: {
    validate() {
      this.fieldErrors.email = emailError(this.form.email);
      this.fieldErrors.password = this.form.password ? "" : "Vui lòng nhập mật khẩu!";
      return !this.fieldErrors.email && !this.fieldErrors.password;
    },
    async handleLogin() {
      this.errorMsg = "";
      if (!this.validate()) return;

      this.loading = true;
      try {
        const res = await loginAPI(this.form);
        setUser(res.data);
        if (res.data?.token) setToken(res.data.token);
        window.dispatchEvent(new Event("user-logged-in"));
        toast.success(`Xin chào ${res.data.fullname || res.data.email}!`);

        const role = res.data.role ? res.data.role.toLowerCase() : "";
        if (role === "admin" || role === "staff") {
          this.$router.push("/admin");
        } else {
          this.$router.push("/");
        }
      } catch (err) {
        this.errorMsg =
          err.response?.data?.message || "Email hoặc mật khẩu không đúng!";
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
.demo-accounts {
  line-height: 1.5;
}
</style>
