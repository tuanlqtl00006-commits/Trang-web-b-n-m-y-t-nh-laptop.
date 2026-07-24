<template>
  <div>
    <h4 class="fw-bold mb-4">Quản lý khách hàng</h4>

    <table class="table align-middle bg-white">
      <thead>
        <tr>
          <th>ID</th>
          <th>Họ tên</th>
          <th>Email</th>
          <th>Điện thoại</th>
          <th>Vai trò</th>
          <th>Trạng thái</th>
          <th style="width: 100px"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="u in items" :key="u.id">
          <td>{{ u.id }}</td>
          <td>{{ u.fullname }}</td>
          <td>{{ u.email }}</td>
          <td>{{ u.phone }}</td>
          <td>
            <span class="badge" :class="u.role && u.role.toLowerCase() === 'admin' ? 'bg-dark' : 'bg-info text-dark'">
              {{ u.role && u.role.toLowerCase() === "admin" ? "Quản trị" : "Khách hàng" }}
            </span>
          </td>
          <td>
            <span class="badge" :class="u.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'">
              {{ u.status === "ACTIVE" ? "Hoạt động" : "Ngừng" }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm btn-outline-danger" @click="remove(u.id)">
              <i class="bi bi-trash"></i>
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { getUsers, deleteUser } from "@/api/user";

export default {
  name: "AdminCustomers",
  data() {
    return {
      items: [],
    };
  },
  mounted() {
    this.loadItems();
  },
  methods: {
    async loadItems() {
      const res = await getUsers();
      this.items = res.data;
    },
    async remove(id) {
      if (!confirm("Bạn có chắc muốn xóa khách hàng này?")) return;
      await deleteUser(id);
      await this.loadItems();
    },
  },
};
</script>
