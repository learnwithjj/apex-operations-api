package com.apex.operations.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity
@Table(name = "operations")
public class Operation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(length = 1000)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OperationStatus status;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @PrePersist void onCreate() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OperationStatus getStatus() { return status; }
    public void setStatus(OperationStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
