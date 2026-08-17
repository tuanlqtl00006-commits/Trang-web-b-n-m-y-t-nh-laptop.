import { createRouter, createWebHistory } from "vue-router";
import { getUser } from "@/utils/session";

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
import AdminStaff from "../views/admin/Staff.vue";
import AdminSales from "../views/admin/Sales.vue";
import AdminHiddenOrders from "../views/admin/HiddenOrders.vue";

// Roles allowed into the /admin area at all ("bán hàng"): admin + nhân viên (staff).
// Some sub-routes are further restricted to admin only (see meta.adminOnly below) —
// account/staff management should not be reachable by a staff account.
const STORE_ROLES = ["admin", "staff"];

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
    meta: { requiresAuth: true, roles: STORE_ROLES },
    children: [
      { path: "", name: "AdminDashboard", component: AdminDashboard },
      { path: "products", name: "AdminProducts", component: AdminProducts },
      { path: "category", name: "AdminCategory", component: AdminCategory },
      { path: "brands", name: "AdminBrand", component: AdminBrand },
      { path: "cpus", name: "AdminCpu", component: AdminCpu },
      { path: "rams", name: "AdminRam", component: AdminRam },
      { path: "orders", name: "AdminOrders", component: AdminOrders },
      { path: "orders/:id", name: "AdminOrderDetail", component: AdminOrderDetail },
      // Bán hàng tại quầy: nhân viên tạo đơn hàng trực tiếp cho khách.
      { path: "sales", name: "AdminSales", component: AdminSales },
      // Account/staff management stays admin-only ("quyền bán hàng" cho nhân viên
      // không bao gồm quản lý tài khoản).
      { path: "customers", name: "AdminCustomers", component: AdminCustomers, meta: { adminOnly: true } },
      { path: "staff", name: "AdminStaff", component: AdminStaff, meta: { adminOnly: true } },
      { path: "hidden-orders", name: "AdminHiddenOrders", component: AdminHiddenOrders, meta: { adminOnly: true } },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const user = getUser();
  const role = user?.role?.toLowerCase();

  if (to.meta.requiresAuth && !user) {
    next("/login");
    return;
  }

  if (to.meta.roles && !to.meta.roles.includes(role)) {
    next("/");
    return;
  }

  if (to.meta.adminOnly && role !== "admin") {
    next("/admin");
    return;
  }

  next();
});

export default router;
