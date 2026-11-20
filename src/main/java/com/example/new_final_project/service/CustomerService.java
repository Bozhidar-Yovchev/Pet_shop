package com.example.new_final_project.service;

import com.example.new_final_project.config.PasswordHasher;
import com.example.new_final_project.dto.ProfileForm;
import com.example.new_final_project.model.Customer;
import com.example.new_final_project.Repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;

    public CustomerService(CustomerRepository customerRepository, PasswordHasher passwordHasher) {
        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional(readOnly = true)
    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Transactional(readOnly = true)
    public boolean emailTakenByOther(String email, Long customerId) {
        return customerRepository.existsByEmailAndIdNot(email.trim().toLowerCase(), customerId);
    }

    @Transactional(readOnly = true)
    public boolean passwordMatches(Long customerId, String rawPassword) {
        return passwordHasher.matches(rawPassword, getById(customerId).getPassword());
    }

    @Transactional
    public Customer updateProfile(Long customerId, ProfileForm form) {
        Customer customer = getById(customerId);
        customer.setFullName(form.getFullName().trim());
        customer.setEmail(form.getEmail().trim().toLowerCase());
        return customerRepository.save(customer);
    }

    @Transactional
    public void changePassword(Long customerId, String newPassword) {
        Customer customer = getById(customerId);
        customer.setPassword(passwordHasher.encode(newPassword));
        customerRepository.save(customer);
    }
}