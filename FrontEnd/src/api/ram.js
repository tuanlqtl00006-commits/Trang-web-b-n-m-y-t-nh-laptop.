import api from "./axios";

export const getRams = () => api.get("/rams");
export const getActiveRams = () => api.get("/rams/active");
export const getRamById = (id) => api.get(`/rams/${id}`);
export const createRam = (data) => api.post("/rams", data);
export const updateRam = (id, data) => api.put(`/rams/${id}`, data);
export const deleteRam = (id) => api.delete(`/rams/${id}`);
