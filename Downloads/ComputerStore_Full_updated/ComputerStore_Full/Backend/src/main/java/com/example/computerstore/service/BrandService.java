package com.example.computerstore.service;

import com.example.computerstore.model.Brand;
import com.example.computerstore.repository.BrandRepository;
import com.example.computerstore.util.ValidationUtil;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    private final BrandRepository repository;

    public BrandService(BrandRepository repository) {
        this.repository = repository;
    }

    public List<Brand> getAll() {
        return repository.findAll();
    }

    public List<Brand> getActive() {
        return repository.findByStatus("ACTIVE");
    }

    public Brand getById(Long id) {
        Optional<Brand> brand = repository.findById(id);
        return brand.orElse(null);
    }

    public Brand create(Brand brand) {
        ValidationUtil.requireNotBlank(brand.getBrandName(), "tên thương hiệu");
        if (nameExists(brand.getBrandName(), null)) {
            throw new IllegalArgumentException("Tên thương hiệu đã tồn tại, vui lòng chọn tên khác!");
        }
        brand.setStatus("ACTIVE");
        return repository.save(brand);
    }

    public Brand update(Long id, Brand brand) {
        Optional<Brand> existing = repository.findById(id);
        if (existing.isPresent()) {
            ValidationUtil.requireNotBlank(brand.getBrandName(), "tên thương hiệu");
            if (nameExists(brand.getBrandName(), id)) {
                throw new IllegalArgumentException("Tên thương hiệu đã tồn tại, vui lòng chọn tên khác!");
            }
            Brand b = existing.get();
            b.setBrandName(brand.getBrandName());
            b.setStatus(brand.getStatus());
            return repository.save(b);
        }
        return null;
    }

    private boolean nameExists(String name, Long excludeId) {
        return repository.findAll().stream()
                .anyMatch(b -> b.getBrandName() != null
                        && b.getBrandName().trim().equalsIgnoreCase(name.trim())
                        && !b.getId().equals(excludeId));
    }

    public void delete(Long id) {
        Optional<Brand> existing = repository.findById(id);
        if (existing.isPresent()) {
            Brand b = existing.get();
            b.setStatus("INACTIVE");
            repository.save(b);
        }
    }
}
