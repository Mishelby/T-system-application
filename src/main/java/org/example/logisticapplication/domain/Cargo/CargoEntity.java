package org.example.logisticapplication.domain.Cargo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "cargos")
public class CargoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cargo_number",nullable = false)
    private Integer cargoNumber;

    @Column(name = "cargo_name",nullable = false)
    private String cargoName;

    @Column(name = "cargo_mass",nullable = false)
    private Integer cargoMass;

    @Column(name = "cargo_status")
    @Pattern(regexp = "PREPARED|SHIPPED|DELIVERED", message = "Invalid driver status")
    private String cargoStatus;

    public CargoEntity(
            Long id,
            Integer cargoNumber,
            String cargoName,
            Integer cargoMass,
            String cargoStatus
    ) {
        this.id = id;
        this.cargoNumber = cargoNumber;
        this.cargoName = cargoName;
        this.cargoMass = cargoMass;
        this.cargoStatus = cargoStatus;
    }
    public CargoEntity() {}
}
