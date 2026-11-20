package com.example.new_final_project.Repository;

import com.example.new_final_project.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomerId(Long customerId);
    Optional<Address> findByIdAndCustomerId(Long id, Long customerId);
}