<template>
  <div class="admin-layout">
    <nav class="navbar navbar-dark bg-dark px-3">
      <span class="navbar-brand"><i class="bi bi-cpu me-1"></i>ComputerStore Admin</span>
      <div class="d-flex align-items-center gap-2">
        <span class="badge role-badge" :class="isAdmin ? 'bg-warning text-dark' : 'bg-info text-dark'">
          <i class="bi bi-person-badge me-1"></i>{{ isAdmin ? "Quản trị viên" : "Nhân viên bán hàng" }}
        </span>

        <!-- Dropdown tên nhân viên -->
        <div class="dropdown">
          <button
            class="btn btn-sm btn-outline-light dropdown-toggle d-flex align-items-center gap-1"
            type="button"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            <i class="bi bi-person-circle"></i>
            <span class="d-none d-md-inline">{{ user?.fullname || user?.email || "Tài khoản" }}</span>
          </button>
          <ul class="dropdown-menu dropdown-menu-end">
            <li class="dropdown-header text-muted small px-3 py-1">
              {{ user?.email }}
            </li>
            <li><hr class="dropdown-divider my-1" /></li>
            <li>
              <router-link class="dropdown-item" to="/profile">
                <i class="bi bi-person me-2"></i>Hồ sơ cá nhân
              </router-link>
            </li>
            <li><hr class="dropdown-divider my-1" /></li>
            <li>
              <button class="dropdown-item text-danger" @click="logout">
                <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
              </button>
            </li>
          </ul>
        </div>

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
          <li class="nav-item">
            <RouterLink class="nav-link" to="/admin/sales">
              <i class="bi bi-cash-coin me-2"></i>Bán hàng tại quầy
            </RouterLink>
          </li>
          <!-- Chỉ admin mới thấy quản lý tài khoản -->
          <li class="nav-item" v-if="isAdmin">
            <RouterLink class="nav-link" to="/admin/customers">
              <i class="bi bi-people me-2"></i>Tài khoản khách hàng
            </RouterLink>
          </li>
          <li class="nav-item" v-if="isAdmin">
            <RouterLink class="nav-link" to="/admin/staff">
              <i class="bi bi-person-badge me-2"></i>Tài khoản nhân viên
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
import { getUser, clearSession } from "@/utils/session";

export default {
  name: "AdminLayout",
  data() {
    return {
      user: getUser(),
    };
  },
  computed: {
    isAdmin() {
      return this.user?.role?.toLowerCase() === "admin";
    },
  },
  methods: {
    logout() {
      clearSession();
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
.dropdown-menu {
  min-width: 200px;
}
</style>
