<template>
  <div class="d-flex justify-content-center align-items-center auth-wrapper">
    <div class="card shadow-sm p-4" style="width: 380px">
      <h4 class="text-center fw-bold mb-4">
        <i class="bi bi-cpu me-1"></i>Đăng nhập
      </h4>

      <div v-if="errorMsg" class="alert alert-danger py-2">{{ errorMsg }}</div>

      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input v-model="form.email" type="email" class="form-control" required />
        </div>
        <div class="mb-3">
          <label class="form-label">Mật khẩu</label>
          <input v-model="form.password" type="password" class="form-control" required />
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
    </div>
  </div>
</template>

<script>
import { loginAPI } from "@/api/auth";

export default {
  name: "Login",
  data() {
    return {
      form: { email: "", password: "" },
      errorMsg: "",
      loading: false,
    };
  },
  methods: {
    async handleLogin() {
      this.errorMsg = "";
      this.loading = true;
      try {
        const res = await loginAPI(this.form);
        localStorage.setItem("user", JSON.stringify(res.data));
        window.dispatchEvent(new Event("user-logged-in"));

        const role = res.data.role ? res.data.role.toLowerCase() : "";
        if (role === "admin" || role === "employee") {
          this.$router.push("/admin");
        } else {
          this.$router.push("/");
        }
      } catch (err) {
        this.errorMsg =
          err.response?.data || "Email hoặc mật khẩu không đúng!";
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
