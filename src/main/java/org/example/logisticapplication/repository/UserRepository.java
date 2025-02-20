package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.User.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = {"roleEntities"})
    @Query("""
            SELECT (COUNT(u) > 0)
            FROM UserEntity u
            WHERE u.email = :email            
            """)
    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"roleEntities"})
    @Query("""
            SELECT (COUNT(u) > 0)
            FROM UserEntity u
            WHERE u.username = :userName            
            """)
    boolean existsByUserName(String userName);

    @Query("SELECT u FROM UserEntity u WHERE u.email =:email")
    Optional<UserEntity> findUserIdEntityByEmail(String email);

    @Query("""
            SELECT (COUNT(u) > 0) 
            FROM UserEntity u
            WHERE u.email = :email
            AND u.password = :password
            """)
    int existsByEmailAndPassword(
            @Param("email") String email,
            @Param("password") String password
    );

    Optional<UserEntity> findUserEntityByEmail(String email);

    @Query("""
            SELECT u 
            FROM UserEntity u
            LEFT JOIN FETCH u.roleEntities
            WHERE u.username = :name
            """)
    Optional<UserEntity> findByName(
           @Param("name") String username
    );
}
