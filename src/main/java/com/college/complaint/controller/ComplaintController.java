package com.college.complaint.controller;

import com.college.complaint.model.AppUser;
import com.college.complaint.model.Complaint;
import com.college.complaint.repository.ComplaintRepository;
import com.college.complaint.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ComplaintController {
    private final ComplaintRepository complaints;
    private final UserRepository users;

    private static final Set<String> CATEGORIES =
        Set.of("Academic", "Infrastructure", "Hostel", "Transport", "Canteen", "Other");
    private static final Set<String> PRIORITIES = Set.of("Low", "Medium", "High");
    private static final Set<String> STATUSES = Set.of("Pending", "In Progress", "Resolved");

    public ComplaintController(ComplaintRepository complaints, UserRepository users) {
        this.complaints = complaints;
        this.users = users;
    }

    public record ComplaintRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 3000) String description,
        @NotBlank String category,
        String priority
    ) {}

    public record StatusRequest(@NotBlank String status) {}

    @PostMapping("/complaints")
    public Complaint create(@Valid @RequestBody ComplaintRequest request, HttpSession session) {
        AppUser user = requireUser(session);
        requireStudent(user);
        String priority = request.priority() == null || request.priority().isBlank()
            ? "Medium" : request.priority();
        if (!CATEGORIES.contains(request.category()) || !PRIORITIES.contains(priority)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category or priority.");
        }
        return complaints.save(new Complaint(request.title().trim(), request.description().trim(),
            request.category(), priority, user.getId(), user.getFullName()));
    }

    @GetMapping("/complaints/mine")
    public List<Complaint> mine(HttpSession session) {
        AppUser user = requireUser(session);
        requireStudent(user);
        return complaints.findByStudentIdOrderByCreatedAtDesc(user.getId());
    }

    @GetMapping("/admin/complaints")
    public List<Complaint> all(HttpSession session) {
        requireAdmin(requireUser(session));
        return complaints.findAllByOrderByCreatedAtDesc();
    }

    @PatchMapping("/admin/complaints/{id}/status")
    public Complaint updateStatus(@PathVariable Long id,
                                  @Valid @RequestBody StatusRequest request,
                                  HttpSession session) {
        requireAdmin(requireUser(session));
        if (!STATUSES.contains(request.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status.");
        }
        Complaint complaint = complaints.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found."));
        complaint.setStatus(request.status());
        return complaints.save(complaint);
    }

    private AppUser requireUser(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (id == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in.");
        return users.findById((Long) id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired."));
    }

    private void requireStudent(AppUser user) {
        if (!"STUDENT".equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student access only.");
        }
    }

    private void requireAdmin(AppUser user) {
        if (!"ADMIN".equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator access only.");
        }
    }
}
