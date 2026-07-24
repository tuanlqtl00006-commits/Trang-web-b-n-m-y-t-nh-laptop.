package com.example.computerstore.dto;

public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String image;
    private String description;
    private String status;
    private String ssd;
    private String gpu;
    private String screen;
    private String os;
    private Long categoryId;
    private Long brandId;
    private Long cpuId;
    private Long ramId;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String name, Double price, Integer stock, String image, String description,
                      String status, String ssd, String gpu, String screen, String os,
                      Long categoryId, Long brandId, Long cpuId, Long ramId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.image = image;
        this.description = description;
        this.status = status;
        this.ssd = ssd;
        this.gpu = gpu;
        this.screen = screen;
        this.os = os;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.cpuId = cpuId;
        this.ramId = ramId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSsd() {
        return ssd;
    }

    public void setSsd(String ssd) {
        this.ssd = ssd;
    }

    public String getGpu() {
        return gpu;
    }

    public void setGpu(String gpu) {
        this.gpu = gpu;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCpuId() {
        return cpuId;
    }

    public void setCpuId(Long cpuId) {
        this.cpuId = cpuId;
    }

    public Long getRamId() {
        return ramId;
    }

    public void setRamId(Long ramId) {
        this.ramId = ramId;
    }
}
