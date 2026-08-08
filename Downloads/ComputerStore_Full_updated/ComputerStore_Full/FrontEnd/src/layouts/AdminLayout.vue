<template>
  <div class="admin-layout">
    <nav class="navbar navbar-dark bg-dark px-3">
      <span class="navbar-brand"><i class="bi bi-cpu me-1"></i>ComputerStore Admin</span>
      <div class="d-flex align-items-center gap-2">
        <span class="badge role-badge" :class="isAdmin ? 'bg-warning text-dark' : 'bg-info text-dark'">
          <i class="bi bi-person-badge me-1"></i>{{ isAdmin ? "Quản trị viên" : "Nhân viên bán hàng" }}
        </span>
        <router-link to="/" class="btn btn-sm btn-outline-light">
          <i class="bi bi-shop"></i> Về trang bán hàng
        </router-link>
      </div>
    </nav>

    <div class="d-flex">
      <aside class="sidebar bg-light border-end">
        <ul class="nav flex-column p-3">
          <li class="nav-item">
            <router-link class="nav-link" to="/admin" exact-active-class="router-link-exact-active">
              <i class="bi bi-speedometer2 me-2"></i>Dashboard
            </router-link>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/admin/products">
              <i class="bi bi-laptop me-2"></i>Quản lý sản phẩm
            </RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/admin/category">
              <i class="bi bi-tags me-2"></i>Quản lý danh mục
            </RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/admin/brands">
              <i class="bi bi-award me-2"></i>Quản lý thương hiệu
            </RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/admin/cpus">
              <i class="bi bi-cpu-fill me-2"></i>Quản lý CPU
            </RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/admin/rams">
              <i class="bi bi-memory me-2"></i>Quản lý RAM
            </RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link" to="/admin/orders">
              <i class="bi bi-bag-check me-2"></i>Quản lý đơn hàng
            </RouterLink>
          </li>
          <li class="nav-item" v-if="isAdmin">
            <RouterLink class="nav-link" to="/admin/customers">
              <i class="bi bi-people me-2"></i>Quản lý tài khoản
            </RouterLink>
          </li>
          <li class="nav-item mt-4">
            <button class="btn btn-danger w-100" @click="logout">
              <i class="bi bi-box-arrow-right me-1"></i>Đăng xuất
            </button>
          </li>
        </ul>
      </aside>

      <main class="content p-4 flex-fill">
        <router-view />
      </main>
    </div>

    <footer class="bg-dark text-white text-center py-2">
      © 2026 ComputerStore
    </footer>
  </div>
</template>

<script>
export default {
  name: "AdminLayout",
  data() {
    return {
      user: JSON.parse(localStorage.getItem("user") || "null"),
    };
  },
  computed: {
    isAdmin() {
      return this.user?.role?.toLowerCase() === "admin";
    },
  },
  methods: {
    logout() {
      localStorage.removeItem("user");
      localStorage.removeItem("token");
      this.$router.push("/login");
    },
  },
};
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}
.sidebar {
  width: 240px;
  min-width: 240px;
  flex-shrink: 0;
  min-height: calc(100vh - 56px - 40px);
}
.nav-link {
  color: #000;
}
.nav-link.router-link-exact-active {
  font-weight: bold;
  background-color: #000;
  color: #fff;
  border-radius: 4px;
}
.role-badge {
  font-weight: 600;
  padding: 6px 10px;
}
</style>
