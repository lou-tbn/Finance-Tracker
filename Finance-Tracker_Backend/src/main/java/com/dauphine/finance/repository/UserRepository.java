package com.dauphine.finance.repository;

import com.dauphine.finance.model.Category;
import com.dauphine.finance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("""
            Select c
            From User c
            Where UPPER(c.username) LIKE UPPER(CONCAT('%', :username, '%'))
            """)
    List<User> findAllLikeName(@Param("username") String username);

    @Query("""
        Select Exists (Select c from User c Where UPPER(c.username) = UPPER(:username))
    """
    )
    boolean existsByName(@Param("username") String username);

    @Query("""
        Select Exists (Select c from User c Where UPPER(c.email) = UPPER(:email))
    """
    )
    boolean existsByEmail(@Param("email") String email);
}