package com.example.computerstore.repository;

import com.example.computerstore.model.Cpu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CpuRepository extends JpaRepository<Cpu, Long> {
    List<Cpu> findByStatus(String status);
}
