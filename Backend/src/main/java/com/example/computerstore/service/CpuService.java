package com.example.computerstore.service;

import com.example.computerstore.model.Cpu;
import com.example.computerstore.repository.CpuRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CpuService {
    private final CpuRepository repository;

    public CpuService(CpuRepository repository) {
        this.repository = repository;
    }

    public List<Cpu> getAll() {
        return repository.findAll();
    }

    public List<Cpu> getActive() {
        return repository.findByStatus("ACTIVE");
    }

    public Cpu getById(Long id) {
        Optional<Cpu> cpu = repository.findById(id);
        return cpu.orElse(null);
    }

    public Cpu create(Cpu cpu) {
        cpu.setStatus("ACTIVE");
        return repository.save(cpu);
    }

    public Cpu update(Long id, Cpu cpu) {
        Optional<Cpu> existing = repository.findById(id);
        if (existing.isPresent()) {
            Cpu c = existing.get();
            c.setCpuName(cpu.getCpuName());
            c.setStatus(cpu.getStatus());
            return repository.save(c);
        }
        return null;
    }

    public void delete(Long id) {
        Optional<Cpu> existing = repository.findById(id);
        if (existing.isPresent()) {
            Cpu c = existing.get();
            c.setStatus("INACTIVE");
            repository.save(c);
        }
    }
}
