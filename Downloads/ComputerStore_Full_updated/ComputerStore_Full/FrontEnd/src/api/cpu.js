import api from "./axios";

export const getCpus = () => api.get("/cpus");
export const getActiveCpus = () => api.get("/cpus/active");
export const getCpuById = (id) => api.get(`/cpus/${id}`);
export const createCpu = (data) => api.post("/cpus", data);
export const updateCpu = (id, data) => api.put(`/cpus/${id}`, data);
export const deleteCpu = (id) => api.delete(`/cpus/${id}`);
