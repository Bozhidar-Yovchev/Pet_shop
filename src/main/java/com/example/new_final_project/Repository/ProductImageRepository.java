package com.example.new_final_project.Repository;

import com.example.new_final_project.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}