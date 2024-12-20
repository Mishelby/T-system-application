package org.example.logisticapplication.domain.Cargo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cargo")
@Getter
@Setter
public class CargoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "weight_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "status", nullable = true)
    @Pattern(regexp = "PREPARED|SHIPPED|DELIVERED", message = "Invalid cargo status")
    private String status;

    public CargoEntity(
            Long id,
            String number,
            String name,
            BigDecimal weightKg,
            String status
    ) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.weightKg = weightKg;
        this.status = status;
    }

    public CargoEntity(
            String number,
            String name,
            BigDecimal weightKg
    ) {
        this.number = number;
        this.name = name;
        this.weightKg = weightKg;
    }

    public CargoEntity() {}

}
