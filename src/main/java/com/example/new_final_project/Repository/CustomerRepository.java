package com.example.new_final_project.Repository;

import com.example.new_final_project.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
    Optional<Customer> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}