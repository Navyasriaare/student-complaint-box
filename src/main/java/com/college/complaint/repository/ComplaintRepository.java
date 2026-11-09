package com.college.complaint.repository;

import com.college.complaint.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<Complaint> findAllByOrderByCreatedAtDesc();
}
