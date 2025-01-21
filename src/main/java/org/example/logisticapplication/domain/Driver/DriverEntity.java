package org.example.logisticapplication.domain.Driver;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;

@Entity
@Table(name = "driver")
@Getter
@Setter
public class DriverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String secondName;

    @Column(name = "person_number", unique = true, nullable = false)
    private Long personNumber;

    @Column(name = "num_of_hours_worked")
    private Integer numberOfHoursWorked;

    @Column(name = "driver_status", nullable = false)
    @Pattern(regexp = "ACTIVE|INACTIVE|SUSPENDED", message = "Invalid driver status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_city_id")
    private CityEntity currentCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_truck_id")
    private TruckEntity currentTruck;

    public DriverEntity(
            Long id,
            String name,
            String secondName,
            Long personNumber,
            Integer numberOfHoursWorked,
            String status,
            CityEntity currentCity,
            TruckEntity currentTruck
    ) {
        this.id = id;
        this.name = name;
        this.secondName = secondName;
        this.personNumber = personNumber;
        this.numberOfHoursWorked = numberOfHoursWorked;
        this.status = status;
        this.currentCity = currentCity;
        this.currentTruck = currentTruck;
    }

    public DriverEntity() {}
}
