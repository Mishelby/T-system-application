package org.example.logisticapplication.domain.DriverStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "driver_status")
@Getter
@Setter
public class DriverStatusEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "REST|ON_SHIFT|DRIVING|THE_SECOND_DRIVER|LOADING_UNLOADING_OPERATIONS", message = "Invalid driver status")
    @Column(name = "status")
    private String status;

    public DriverStatusEntity(String status) {
        this.status = status;
    }

    public DriverStatusEntity() {
    }

}
