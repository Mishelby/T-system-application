package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Admin.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    @Query("""
            SELECT a
            FROM AdminEntity a
            WHERE a.email = :email                        
            """)
    Optional<AdminEntity> findByEmail(
            @Param("email") String email
    );
}
