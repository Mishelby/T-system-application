package org.example.logisticapplication.domain.DriverStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.Driver.DriverStatus;

@Entity
@Table(name = "driver_status")
@Getter
@Setter
@EqualsAndHashCode(of = "status")
public class DriverStatusEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DriverStatus status;

    public DriverStatusEntity(
            DriverStatus status
    ) {
        this.status = status;
    }

    public DriverStatusEntity() {
    }

}
