package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.User.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("""
            SELECT (COUNT(u) > 0)
            FROM UserEntity u
            WHERE u.email = :email            
            """)
    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.email =:email")
    Optional<UserEntity> findUserIdEntityByEmail(String email);
}
