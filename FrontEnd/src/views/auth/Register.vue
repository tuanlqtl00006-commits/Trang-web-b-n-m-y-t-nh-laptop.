<template>
  <div class="d-flex justify-content-center align-items-center auth-wrapper">
    <div class="card shadow-sm p-4" style="width: 400px">
      <h4 class="text-center fw-bold mb-4">
        <i class="bi bi-cpu me-1"></i>Đăng ký tài khoản
      </h4>

      <div v-if="errorMsg" class="alert alert-danger py-2">{{ errorMsg }}</div>
      <div v-if="successMsg" class="alert alert-success py-2">{{ successMsg }}</div>

      <form @submit.prevent="handleRegister">
        <div class="mb-3">
          <label class="form-label">Họ và tên</label>
          <input v-model="form.fullname" type="text" class="form-control" required />
        </div>
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input v-model="form.email" type="email" class="form-control" required />
        </div>
        <div class="mb-3">
          <label class="form-label">Số điện thoại</label>
          <input v-model="form.phone" type="text" class="form-control" required />
        </div>
        <div class="mb-3">
          <label class="form-label">Mật khẩu</label>
          <input v-model="form.password" type="password" class="form-control" required />
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

export default {
  name: "Register",
  data() {
    return {
      form: { fullname: "", email: "", phone: "", password: "" },
      errorMsg: "",
      successMsg: "",
      loading: false,
    };
  },
  methods: {
    async handleRegister() {
      this.errorMsg = "";
      this.successMsg = "";
      this.loading = true;
      try {
        await registerAPI(this.form);
        this.successMsg = "Đăng ký thành công! Đang chuyển sang đăng nhập...";
        setTimeout(() => this.$router.push("/login"), 1200);
      } catch (err) {
        this.errorMsg = err.response?.data || "Email đã tồn tại!";
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
