package com.college.complaint.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String priority = "Medium";

    @Column(nullable = false)
    private String status = "Pending";

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Complaint() {}

    public Complaint(String title, String description, String category,
                     String priority, Long studentId, String studentName) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority == null || priority.isBlank() ? "Medium" : priority;
        this.status = "Pending";
        this.studentId = studentId;
        this.studentName = studentName;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getPriority() { return priority == null || priority.isBlank() ? "Medium" : priority; }
    public String getStatus() { return status; }
    public Long getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setStatus(String status) { this.status = status; }
}
