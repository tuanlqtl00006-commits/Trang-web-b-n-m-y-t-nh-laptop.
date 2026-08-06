package com.example.computerstore.repository;

import com.example.computerstore.model.Ram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RamRepository extends JpaRepository<Ram, Long> {
    List<Ram> findByStatus(String status);
}
