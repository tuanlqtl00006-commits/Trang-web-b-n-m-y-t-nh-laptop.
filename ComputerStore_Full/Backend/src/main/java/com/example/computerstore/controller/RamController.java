package com.example.computerstore.controller;

import com.example.computerstore.model.Ram;
import com.example.computerstore.service.RamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rams")
@CrossOrigin("*")
public class RamController {
    private final RamService service;

    public RamController(RamService service) {
        this.service = service;
    }

    @GetMapping
    public List<Ram> getAll() {
        return service.getAll();
    }

    @GetMapping("/active")
    public List<Ram> getActive() {
        return service.getActive();
    }

    @GetMapping("/{id}")
    public Ram getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Ram> create(@RequestBody Ram ram) {
        Ram created = service.create(ram);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ram> update(@PathVariable Long id, @RequestBody Ram ram) {
        Ram updated = service.update(id, ram);
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
