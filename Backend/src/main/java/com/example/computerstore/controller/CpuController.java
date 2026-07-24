package com.example.computerstore.controller;

import com.example.computerstore.model.Cpu;
import com.example.computerstore.service.CpuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cpus")
@CrossOrigin("*")
public class CpuController {
    private final CpuService service;

    public CpuController(CpuService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cpu> getAll() {
        return service.getAll();
    }

    @GetMapping("/active")
    public List<Cpu> getActive() {
        return service.getActive();
    }

    @GetMapping("/{id}")
    public Cpu getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Cpu> create(@RequestBody Cpu cpu) {
        Cpu created = service.create(cpu);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cpu> update(@PathVariable Long id, @RequestBody Cpu cpu) {
        Cpu updated = service.update(id, cpu);
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
}
