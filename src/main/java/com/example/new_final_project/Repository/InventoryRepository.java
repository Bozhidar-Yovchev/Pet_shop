package com.example.new_final_project.Repository;

import com.example.new_final_project.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Returns 1 if the stock was decreased, 0 if there was not enough
    @Modifying(flushAutomatically = true)
    @Query("update Inventory i set i.quantity = i.quantity - :qty where i.id = :id and i.quantity >= :qty")
    int decreaseIfAvailable(@Param("id") Long id, @Param("qty") int qty);

    // Gives stock back (used when an order is cancelled)
    @Modifying(flushAutomatically = true)
    @Query("update Inventory i set i.quantity = i.quantity + :qty where i.id = :id")
    int increase(@Param("id") Long id, @Param("qty") int qty);
}