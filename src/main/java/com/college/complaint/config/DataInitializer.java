package com.college.complaint.config;

import com.college.complaint.model.AppUser;
import com.college.complaint.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner createDefaultAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (!users.existsByEmailIgnoreCase("admin@college.com")) {
                users.save(new AppUser("admin@college.com", "College Administrator",
                    encoder.encode("Admin@123"), "ADMIN"));
            }
        };
    }
}
