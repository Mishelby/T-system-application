package org.example.logisticapplication.domain.DriverShift;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.Driver.DriverEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "driver_shift")
@Getter
@Setter
public class DriverShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id", nullable = false)
    private DriverEntity driver;

    @Column(name = "shift_start_time", nullable = false)
    private LocalDateTime startShift;

    @Column(name = "shift_end_time")
    private LocalDateTime endShift;

    @Column(name = "hours_worked")
    private Integer hoursWorked;

    public DriverShift() {}
}
