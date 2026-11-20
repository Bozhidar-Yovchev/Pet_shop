package com.example.new_final_project.Repository;

import com.example.new_final_project.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByActiveTrue();
    List<Discount> findByCodeIsNull();
    Optional<Discount> findByCodeIgnoreCase(String code);
}