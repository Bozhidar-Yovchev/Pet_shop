package com.example.new_final_project.service;

import com.example.new_final_project.config.AppConstants;
import com.example.new_final_project.config.PasswordHasher;
import com.example.new_final_project.dto.RegisterDto;
import com.example.new_final_project.model.Customer;
import com.example.new_final_project.Repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;   // field
    private final PasswordHasher passwordEncoder;          // field

    public AuthService(CustomerRepository customerRepository,
                       PasswordHasher passwordEncoder) {    // constructor
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameTaken(String username) {
        return customerRepository.existsByUsername(username);
    }

    public boolean emailTaken(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Transactional
    public Customer register(RegisterDto dto) {
        Customer customer = new Customer(
                dto.getFullName().trim(),
                dto.getUsername().trim(),
                dto.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(dto.getPassword()),
                AppConstants.Role.CUSTOMER,
                "REGULAR");
        return customerRepository.save(customer);
    }

    public Optional<Customer> authenticate(String username, String rawPassword) {
        return customerRepository.findByUsername(username.trim())
                .filter(c -> passwordEncoder.matches(rawPassword, c.getPassword()));
    }
}