package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.OrderProfileInfo.UserProfileInfoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileInfoRepository extends JpaRepository<UserProfileInfoEntity, Long> {

    @EntityGraph(attributePaths = "user")
    @Query("""
            SELECT upi
            FROM UserProfileInfoEntity upi
            WHERE upi.user.id = :userId                        
            """)
    Optional<UserProfileInfoEntity> getUserProfileInfoById(Long userId);

    @Query("""
            SELECT (COUNT(upi) > 0)
            FROM UserProfileInfoEntity upi
            WHERE upi.user.id = :userId            
            """)
    boolean existsById(Long userId);
}
