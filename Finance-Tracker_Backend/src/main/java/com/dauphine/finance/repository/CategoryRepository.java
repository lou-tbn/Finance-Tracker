package com.dauphine.finance.repository;

import com.dauphine.finance.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("""
            Select c
            From Category c
            Where UPPER(c.name) LIKE UPPER(CONCAT('%', :name, '%'))
            """)
    List<Category> findAllLikeName(@Param("name") String name);

    boolean existsByNameIgnoreCase(String name);
}
