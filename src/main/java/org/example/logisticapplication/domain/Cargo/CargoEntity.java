package org.example.logisticapplication.domain.Cargo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.RoutePoint.RoutePointEntity;

import java.util.List;

@Entity
@Table(name = "cargos")
@Getter
@Setter
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

    @OneToMany(mappedBy = "cargo",fetch = FetchType.LAZY)
    private List<RoutePointEntity> routePointEntity;

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
