package com.example.computerstore.service;

import com.example.computerstore.dto.ProductDTO;
import com.example.computerstore.model.Product;
import com.example.computerstore.repository.ProductRepository;
import com.example.computerstore.repository.CategoryRepository;
import com.example.computerstore.repository.BrandRepository;
import com.example.computerstore.repository.CpuRepository;
import com.example.computerstore.repository.RamRepository;
import com.example.computerstore.util.ValidationUtil;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CpuRepository cpuRepository;
    private final RamRepository ramRepository;

    public ProductService(ProductRepository repository,
                         CategoryRepository categoryRepository,
                         BrandRepository brandRepository,
                         CpuRepository cpuRepository,
                         RamRepository ramRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.cpuRepository = cpuRepository;
        this.ramRepository = ramRepository;
    }

    public List<ProductDTO> getAll() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getActive() {
        return repository.findByStatus("ACTIVE").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getById(Long id) {
        Optional<Product> product = repository.findById(id);
        return product.map(this::convertToDTO).orElse(null);
    }

    public List<ProductDTO> getByCategory(Long categoryId) {
        return repository.findByCategoryId(categoryId).stream()
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO create(ProductDTO dto) {
        ValidationUtil.requireNotBlank(dto.getName(), "tên sản phẩm");
        ValidationUtil.requireValidPrice(dto.getPrice());
        ValidationUtil.requireValidStock(dto.getStock());
        ValidationUtil.requireValidOs(dto.getOs());

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImage(dto.getImage());
        product.setDescription(dto.getDescription());
        product.setStatus("ACTIVE");
        product.setSsd(dto.getSsd());
        product.setGpu(dto.getGpu());
        product.setScreen(dto.getScreen());
        product.setOs(dto.getOs());

        if (dto.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(dto.getCategoryId()).orElse(null));
        }
        if (dto.getBrandId() != null) {
            product.setBrand(brandRepository.findById(dto.getBrandId()).orElse(null));
        }
        if (dto.getCpuId() != null) {
            product.setCpu(cpuRepository.findById(dto.getCpuId()).orElse(null));
        }
        if (dto.getRamId() != null) {
            product.setRam(ramRepository.findById(dto.getRamId()).orElse(null));
        }

        Product savedProduct = repository.save(product);
        return convertToDTO(savedProduct);
    }

    public ProductDTO update(Long id, ProductDTO dto) {
        Optional<Product> existing = repository.findById(id);
        if (existing.isPresent()) {
            ValidationUtil.requireNotBlank(dto.getName(), "tên sản phẩm");
            ValidationUtil.requireValidPrice(dto.getPrice());
            ValidationUtil.requireValidStock(dto.getStock());
            ValidationUtil.requireValidOs(dto.getOs());

            Product product = existing.get();
            product.setName(dto.getName());
            product.setPrice(dto.getPrice());
            product.setStock(dto.getStock());
            product.setImage(dto.getImage());
            product.setDescription(dto.getDescription());
            product.setStatus(dto.getStatus());
            product.setSsd(dto.getSsd());
            product.setGpu(dto.getGpu());
            product.setScreen(dto.getScreen());
            product.setOs(dto.getOs());

            if (dto.getCategoryId() != null) {
                product.setCategory(categoryRepository.findById(dto.getCategoryId()).orElse(null));
            }
            if (dto.getBrandId() != null) {
                product.setBrand(brandRepository.findById(dto.getBrandId()).orElse(null));
            }
            if (dto.getCpuId() != null) {
                product.setCpu(cpuRepository.findById(dto.getCpuId()).orElse(null));
            }
            if (dto.getRamId() != null) {
                product.setRam(ramRepository.findById(dto.getRamId()).orElse(null));
            }

            Product updatedProduct = repository.save(product);
            return convertToDTO(updatedProduct);
        }
        return null;
    }

    /** Soft delete: hides the product from customers but keeps history (orders, stats) intact. */
    public void hide(Long id) {
        Optional<Product> existing = repository.findById(id);
        if (existing.isPresent()) {
            Product product = existing.get();
            product.setStatus("INACTIVE");
            repository.save(product);
        }
    }

    /** Hard delete: permanently removes the product. Will fail (409) if it is referenced by existing orders. */
    public void hardDelete(Long id) {
        repository.deleteById(id);
    }

    // Kept for backwards compatibility: default delete behaviour is "hide".
    public void delete(Long id) {
        hide(id);
    }

    public ProductDTO createWithImage(Product product) {
        ValidationUtil.requireNotBlank(product.getName(), "tên sản phẩm");
        ValidationUtil.requireValidPrice(product.getPrice());
        ValidationUtil.requireValidStock(product.getStock());
        ValidationUtil.requireValidOs(product.getOs());

        product.setStatus("ACTIVE");
        Product savedProduct = repository.save(product);
        return convertToDTO(savedProduct);
    }

    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getStock(),
            product.getImage(),
            product.getDescription(),
            product.getStatus(),
            product.getSsd(),
            product.getGpu(),
            product.getScreen(),
            product.getOs(),
            product.getCategory() != null ? product.getCategory().getId() : null,
            product.getBrand() != null ? product.getBrand().getId() : null,
            product.getCpu() != null ? product.getCpu().getId() : null,
            product.getRam() != null ? product.getRam().getId() : null
        );
    }
}
