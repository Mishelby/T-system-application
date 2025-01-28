package org.example.logisticapplication.domain.Cargo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

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
    private Long weightKg;

    @Column(name = "status", nullable = false)
    @Pattern(regexp = "PREPARED|SHIPPED|DELIVERED|NOT_SHIPPED", message = "Invalid cargo status")
    private String status;

    public CargoEntity(
            Long id,
            String number,
            String name,
            Long weightKg,
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
            Long weightKg
    ) {
        this.number = number;
        this.name = name;
        this.weightKg = weightKg;
    }

    public CargoEntity() {}

}
