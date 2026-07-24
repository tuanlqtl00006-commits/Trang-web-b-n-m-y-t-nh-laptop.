package com.example.computerstore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rams")
public class Ram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ramName;

    private String status;

    public Ram() {
    }

    public Ram(Long id, String ramName, String status) {
        this.id = id;
        this.ramName = ramName;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRamName() {
        return ramName;
    }

    public void setRamName(String ramName) {
        this.ramName = ramName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
