import api from "./axios";

export const getOrders = () => api.get("/orders");
export const getActiveOrders = () => api.get("/orders/active");
export const getOrderById = (id) => api.get(`/orders/${id}`);
export const getOrdersByUser = (userId) => api.get(`/orders/user/${userId}`);
export const createOrder = (data) => api.post("/orders", data);
export const updateOrder = (id, data) => api.put(`/orders/${id}`, data);
export const deleteOrder = (id) => api.delete(`/orders/${id}`);

// order items
export const getOrderItemsByOrder = (orderId) =>
  api.get(`/order-items/order/${orderId}`);
export const createOrderItem = (data) => api.post("/order-items", data);
