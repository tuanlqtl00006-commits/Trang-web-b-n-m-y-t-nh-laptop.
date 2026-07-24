package com.example.computerstore.service;

import com.example.computerstore.model.Category;
import com.example.computerstore.repository.CategoryRepository;
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
        category.setStatus("ACTIVE");
        return repository.save(category);
    }

    public Category update(Long id, Category category) {
        Optional<Category> existing = repository.findById(id);
        if (existing.isPresent()) {
            Category c = existing.get();
            c.setName(category.getName());
            c.setDescription(category.getDescription());
            c.setStatus(category.getStatus());
            return repository.save(c);
        }
        return null;
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
