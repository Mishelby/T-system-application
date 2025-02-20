package org.example.logisticapplication.domain.ShiftStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.DriverShift.ShiftStatus;

@Entity
@Table(name = "shift_status")
@Getter
@Setter
public class ShiftStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private ShiftStatus status;

    public ShiftStatusEntity(
            ShiftStatus status
    ) {
        this.status = status;
    }

    public ShiftStatusEntity() {}
}
