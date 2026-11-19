package com.example.new_final_project.Repository;

import com.example.new_final_project.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);

    // Pass null for any filter you want to ignore
    @Query("""
            SELECT p FROM Product p
            WHERE (:species IS NULL OR p.animalSpecies = :species)
              AND (:ageGroup IS NULL OR p.ageGroup = :ageGroup)
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
            """)
    List<Product> filter(@Param("species") String species,
                         @Param("ageGroup") String ageGroup,
                         @Param("categoryId") Long categoryId);
}