package com.college.complaint.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    public AppUser() {}

    public AppUser(String email, String fullName, String password, String role) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}
