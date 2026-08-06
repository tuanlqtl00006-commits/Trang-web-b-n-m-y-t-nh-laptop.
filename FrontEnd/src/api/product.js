import api from "./axios";

export const getProducts = () => api.get("/products");
export const getActiveProducts = () => api.get("/products/active");
export const getProductsByCategory = (categoryId) =>
  api.get(`/products/category/${categoryId}`);
export const getProductById = (id) => api.get(`/products/${id}`);
export const createProduct = (data) => api.post("/products", data);
export const updateProduct = (id, data) => api.put(`/products/${id}`, data);
export const deleteProduct = (id) => api.delete(`/products/${id}`);
export const createProductWithImage = (formData) =>
  api.post("/products/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
