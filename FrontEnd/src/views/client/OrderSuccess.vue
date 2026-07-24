<template>
  <div class="text-center py-5">
    <i class="bi bi-check-circle-fill text-success" style="font-size: 4rem"></i>
    <h3 class="fw-bold mt-3">Đặt hàng thành công!</h3>
    <p class="text-muted">Mã đơn hàng của bạn là <strong>#{{ $route.params.id }}</strong></p>
    <div class="d-flex justify-content-center gap-2 mt-4">
      <router-link :to="`/order-detail/${$route.params.id}`" class="btn btn-dark">
        Xem chi tiết đơn hàng
      </router-link>
      <router-link to="/" class="btn btn-outline-dark">Tiếp tục mua sắm</router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: "OrderSuccess",
};
</script>
