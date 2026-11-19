package com.example.new_final_project.Repository;

import com.example.new_final_project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();          // main categories
    List<Category> findByParentId(Long parentId); // subcategories
}