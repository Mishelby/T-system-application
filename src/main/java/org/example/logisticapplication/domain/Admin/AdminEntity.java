package org.example.logisticapplication.domain.Admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.logisticapplication.domain.Role.RoleEntity;
import org.example.logisticapplication.domain.User.UserEntity;

import java.util.Set;

@Entity
@Table(name = "admin")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@EqualsAndHashCode(of = {"username", "email"})
@ToString
public class AdminEntity extends UserEntity {
    @Column(name = "name")
    private String name;

    public AdminEntity(
            String username,
            String password,
            String email,
            Set<RoleEntity> roles,
            String name
    ) {
        super(username, password, email, roles);
        this.name = name;
    }

    public AdminEntity() {
    }
}
