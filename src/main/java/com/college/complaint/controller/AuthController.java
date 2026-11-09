package com.college.complaint.controller;

import com.college.complaint.model.AppUser;
import com.college.complaint.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    public record RegisterRequest(
        @NotBlank @Size(max = 80) String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100) String password
    ) {}

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody RegisterRequest request) {
        if (users.existsByEmailIgnoreCase(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }
        AppUser user = users.save(new AppUser(
            request.email().trim().toLowerCase(),
            request.fullName().trim(),
            encoder.encode(request.password()),
            "STUDENT"
        ));
        return Map.of("message", "Registration successful. Please log in.", "id", user.getId());
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        AppUser user = users.findByEmailIgnoreCase(request.email().trim())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));
        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole());
        return Map.of("id", user.getId(), "fullName", user.getFullName(),
                      "email", user.getEmail(), "role", user.getRole());
    }

    @GetMapping("/me")
    public Map<String, Object> me(HttpSession session) {
        AppUser user = currentUser(session);
        return Map.of("id", user.getId(), "fullName", user.getFullName(),
                      "email", user.getEmail(), "role", user.getRole());
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        session.invalidate();
        return Map.of("message", "Logged out successfully.");
    }

    private AppUser currentUser(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (id == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in.");
        return users.findById((Long) id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired."));
    }
}
