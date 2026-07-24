package com.example.computerstore.controller;

import com.example.computerstore.dto.ProductDTO;
import com.example.computerstore.model.Brand;
import com.example.computerstore.model.Category;
import com.example.computerstore.model.Cpu;
import com.example.computerstore.model.Product;
import com.example.computerstore.model.Ram;
import com.example.computerstore.repository.BrandRepository;
import com.example.computerstore.repository.CategoryRepository;
import com.example.computerstore.repository.CpuRepository;
import com.example.computerstore.repository.ProductRepository;
import com.example.computerstore.repository.RamRepository;
import com.example.computerstore.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("*")
public class ProductController {
    private final ProductService service;
    private final ProductRepository repo;
    private final CategoryRepository categoryRepo;
    private final BrandRepository brandRepo;
    private final CpuRepository cpuRepo;
    private final RamRepository ramRepo;

    public ProductController(ProductService service,
                             ProductRepository repo,
                             CategoryRepository categoryRepo,
                             BrandRepository brandRepo,
                             CpuRepository cpuRepo,
                             RamRepository ramRepo) {
        this.service = service;
        this.repo = repo;
        this.categoryRepo = categoryRepo;
        this.brandRepo = brandRepo;
        this.cpuRepo = cpuRepo;
        this.ramRepo = ramRepo;
    }

    @GetMapping
    public List<ProductDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/active")
    public List<ProductDTO> getActive() {
        return service.getActive();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable Long categoryId) {
        try {
            List<ProductDTO> products = service.getByCategory(categoryId);
            System.out.println("Products found for category " + categoryId + ": " + products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ProductDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO dto) {
        ProductDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        ProductDTO updated = service.update(id, dto);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> createWithImage(
            @RequestParam("name") String name,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("brandId") Long brandId,
            @RequestParam("cpuId") Long cpuId,
            @RequestParam("ramId") Long ramId,
            @RequestParam("stock") Integer stock,
            @RequestParam("status") String status,
            @RequestParam("description") String description,
            @RequestParam(value = "ssd", required = false) String ssd,
            @RequestParam(value = "gpu", required = false) String gpu,
            @RequestParam(value = "screen", required = false) String screen,
            @RequestParam(value = "os", required = false) String os,
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setStock(stock);
        p.setStatus(status);
        p.setDescription(description);
        p.setImage("http://localhost:8080/uploads/" + fileName);
        p.setSsd(ssd);
        p.setGpu(gpu);
        p.setScreen(screen);
        p.setOs(os);

        Category c = categoryRepo.findById(categoryId).orElse(null);
        p.setCategory(c);

        Brand b = brandRepo.findById(brandId).orElse(null);
        p.setBrand(b);

        Cpu cpu = cpuRepo.findById(cpuId).orElse(null);
        p.setCpu(cpu);

        Ram ram = ramRepo.findById(ramId).orElse(null);
        p.setRam(ram);

        return ResponseEntity.status(HttpStatus.CREATED).body(service.createWithImage(p));
    }
}
