package com.example.computerstore.service;

import com.example.computerstore.model.Category;
import com.example.computerstore.repository.CategoryRepository;
import com.example.computerstore.util.ValidationUtil;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> getAll() {
        return repository.findAll();
    }

    public List<Category> getActive() {
        return repository.findByStatus("ACTIVE");
    }

    public Category getById(Long id) {
        Optional<Category> category = repository.findById(id);
        return category.orElse(null);
    }

    public Category create(Category category) {
        ValidationUtil.requireNotBlank(category.getName(), "tên danh mục");
        if (nameExists(category.getName(), null)) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại, vui lòng chọn tên khác!");
        }
        category.setStatus("ACTIVE");
        return repository.save(category);
    }

    public Category update(Long id, Category category) {
        Optional<Category> existing = repository.findById(id);
        if (existing.isPresent()) {
            ValidationUtil.requireNotBlank(category.getName(), "tên danh mục");
            if (nameExists(category.getName(), id)) {
                throw new IllegalArgumentException("Tên danh mục đã tồn tại, vui lòng chọn tên khác!");
            }
            Category c = existing.get();
            c.setName(category.getName());
            c.setDescription(category.getDescription());
            c.setStatus(category.getStatus());
            return repository.save(c);
        }
        return null;
    }

    private boolean nameExists(String name, Long excludeId) {
        return repository.findAll().stream()
                .anyMatch(c -> c.getName() != null
                        && c.getName().trim().equalsIgnoreCase(name.trim())
                        && !c.getId().equals(excludeId));
    }

    public void delete(Long id) {
        Optional<Category> existing = repository.findById(id);
        if (existing.isPresent()) {
            Category c = existing.get();
            c.setStatus("INACTIVE");
            repository.save(c);
        }
    }
}
