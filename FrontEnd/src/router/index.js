import { createRouter, createWebHistory } from "vue-router";

// layouts
import ClientLayout from "../layouts/ClientLayout.vue";
import AdminLayout from "../layouts/AdminLayout.vue";

// auth
import Login from "../views/auth/Login.vue";
import Register from "../views/auth/Register.vue";

// client
import Home from "../views/client/Home.vue";
import ProductDetail from "../views/client/ProductDetail.vue";
import ProductsByCategory from "../views/client/ProductsByCategory.vue";
import Cart from "../views/client/Cart.vue";
import Checkout from "../views/client/Checkout.vue";
import OrderSuccess from "../views/client/OrderSuccess.vue";
import ClientOrders from "../views/client/ClientOrders.vue";
import OrderDetail from "../views/client/OrderDetail.vue";
import Profile from "../views/client/Profile.vue";

// admin
import AdminDashboard from "../views/admin/Dashboard.vue";
import AdminProducts from "../views/admin/Products.vue";
import AdminCategory from "../views/admin/Category.vue";
import AdminBrand from "../views/admin/Brand.vue";
import AdminCpu from "../views/admin/Cpu.vue";
import AdminRam from "../views/admin/Ram.vue";
import AdminOrders from "../views/admin/Orders.vue";
import AdminOrderDetail from "../views/admin/OrderDetail.vue";
import AdminCustomers from "../views/admin/Customers.vue";

const routes = [
  {
    path: "/",
    component: ClientLayout,
    children: [
      { path: "", name: "Home", component: Home },
      { path: "product/:id", name: "ProductDetail", component: ProductDetail },
      { path: "category/:id", name: "ProductsByCategory", component: ProductsByCategory },
      { path: "cart", name: "Cart", component: Cart },
      { path: "checkout", name: "Checkout", component: Checkout },
      { path: "order-success/:id", name: "OrderSuccess", component: OrderSuccess },
      { path: "orders", name: "ClientOrders", component: ClientOrders, meta: { requiresAuth: true } },
      { path: "order-detail/:id", name: "OrderDetail", component: OrderDetail, meta: { requiresAuth: true } },
      { path: "profile", name: "Profile", component: Profile, meta: { requiresAuth: true } },
    ],
  },
  { path: "/login", name: "Login", component: Login },
  { path: "/register", name: "Register", component: Register },
  {
    path: "/admin",
    component: AdminLayout,
    // Cả Admin và Nhân viên đều được vào khu vực quản trị
    meta: { requiresAuth: true, roles: ["admin", "employee"] },
    children: [
      { path: "", name: "AdminDashboard", component: AdminDashboard },
      { path: "products", name: "AdminProducts", component: AdminProducts },
      { path: "category", name: "AdminCategory", component: AdminCategory },
      { path: "brands", name: "AdminBrand", component: AdminBrand },
      { path: "cpus", name: "AdminCpu", component: AdminCpu },
      { path: "rams", name: "AdminRam", component: AdminRam },
      { path: "orders", name: "AdminOrders", component: AdminOrders },
      { path: "orders/:id", name: "AdminOrderDetail", component: AdminOrderDetail },
      // Quản lý khách hàng (tài khoản) chỉ dành riêng cho Admin
      { path: "customers", name: "AdminCustomers", component: AdminCustomers, meta: { roles: ["admin"] } },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem("user") || "null");
  const userRole = user?.role?.toLowerCase();

  if (to.meta.requiresAuth && !user) {
    next("/login");
    return;
  }

  // Kiểm tra quyền: phải thỏa mãn roles ở TẤT CẢ các route cha lẫn route con
  const isForbidden = to.matched.some(
    (record) => record.meta?.roles && !record.meta.roles.includes(userRole)
  );

  if (isForbidden) {
    // Nhân viên/khách vãng lai cố vào trang không có quyền -> đưa về Dashboard admin (nếu là staff) hoặc trang chủ
    next(userRole === "admin" || userRole === "employee" ? "/admin" : "/");
    return;
  }

  next();
});

export default router;
