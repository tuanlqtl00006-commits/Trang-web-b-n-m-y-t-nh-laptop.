package com.example.computerstore.service;

import com.example.computerstore.model.Ram;
import com.example.computerstore.repository.RamRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RamService {
    private final RamRepository repository;

    public RamService(RamRepository repository) {
        this.repository = repository;
    }

    public List<Ram> getAll() {
        return repository.findAll();
    }

    public List<Ram> getActive() {
        return repository.findByStatus("ACTIVE");
    }

    public Ram getById(Long id) {
        Optional<Ram> ram = repository.findById(id);
        return ram.orElse(null);
    }

    public Ram create(Ram ram) {
        ram.setStatus("ACTIVE");
        return repository.save(ram);
    }

    public Ram update(Long id, Ram ram) {
        Optional<Ram> existing = repository.findById(id);
        if (existing.isPresent()) {
            Ram r = existing.get();
            r.setRamName(ram.getRamName());
            r.setStatus(ram.getStatus());
            return repository.save(r);
        }
        return null;
    }

    public void delete(Long id) {
        Optional<Ram> existing = repository.findById(id);
        if (existing.isPresent()) {
            Ram r = existing.get();
            r.setStatus("INACTIVE");
            repository.save(r);
        }
    }
}
