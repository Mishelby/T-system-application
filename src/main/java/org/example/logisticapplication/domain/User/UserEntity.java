package org.example.logisticapplication.domain.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 250)
    private String password;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UserEntity(
            String username,
            String password,
            String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserEntity() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
