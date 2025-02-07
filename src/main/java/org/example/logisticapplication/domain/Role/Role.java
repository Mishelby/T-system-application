package org.example.logisticapplication.domain.Role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.utils.RoleName;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role implements GrantedAuthority{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private RoleName name;

    public Role(RoleName name) {
        this.name = name;
    }

    public Role() {}

    @Override
    public String getAuthority() {
        return name.name();
    }
}
