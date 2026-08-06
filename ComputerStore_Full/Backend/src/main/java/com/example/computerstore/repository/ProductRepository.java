package com.example.computerstore.repository;

import com.example.computerstore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(String status);
    List<Product> findByCategoryIdAndStatus(Long categoryId, String status);
    List<Product> findByBrandIdAndStatus(Long brandId, String status);
    List<Product> findByCategoryId(Long categoryId);
}
