package org.example.logisticapplication.domain.Driver;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;

@Entity
@Table(name = "drivers")
@Getter
@Setter
public class DriverEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "secondName")
    private String secondName;

    @Column(name = "personNumber",unique = true)
    private String personNumber;

    @Column(name = "numOfHoursWorked")
    private Integer numberOfHoursWorked;

    @Column(name = "driver_status")
    @Pattern(regexp = "ACTIVE|INACTIVE|SUSPENDED", message = "Invalid driver status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_city_id")
    private CityEntity currentCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id")
    private TruckEntity currentTruck;

    public DriverEntity(
            Long id,
            String name,
            String secondName,
            String personNumber,
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
