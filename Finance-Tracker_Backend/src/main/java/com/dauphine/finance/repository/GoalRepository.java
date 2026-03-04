package com.dauphine.finance.repository;

import com.dauphine.finance.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
    @Query("""
            Select g
            From Goal g
            Where UPPER(g.title) LIKE UPPER(CONCAT('%', :title, '%'))
            """)
    List<Goal> findAllLikeTitle(@Param("title") String title);

    @Query("""
        SELECT g 
        FROM Goal g 
        WHERE g.user.id = :userId
    """)
    List<Goal> findAllByUserId(@Param("userId") UUID userId);
}
