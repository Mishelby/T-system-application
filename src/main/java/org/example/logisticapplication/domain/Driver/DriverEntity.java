package org.example.logisticapplication.domain.Driver;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.DriverStatus.DriverStatusEntity;
import org.example.logisticapplication.domain.Role.Role;
import org.example.logisticapplication.domain.Truck.TruckEntity;

import java.util.Set;

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

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "person_number", unique = true, nullable = false)
    private Long personNumber;

    @Column(name = "num_of_hours_worked")
    private Double numberOfHoursWorked;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id")
    private DriverStatusEntity status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_city_id")
    private CityEntity currentCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_truck_id")
    private TruckEntity currentTruck;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "driver_roles",
            joinColumns = @JoinColumn(name = "driver_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public DriverEntity(
            Long id,
            String name,
            String secondName,
            String password,
            Long personNumber,
            Double numberOfHoursWorked,
            DriverStatusEntity status,
            CityEntity currentCity,
            TruckEntity currentTruck
    ) {
        this.id = id;
        this.name = name;
        this.secondName = secondName;
        this.password = password;
        this.personNumber = personNumber;
        this.numberOfHoursWorked = numberOfHoursWorked;
        this.status = status;
        this.currentCity = currentCity;
        this.currentTruck = currentTruck;
    }

    public DriverEntity(
            Long id,
            String name,
            String secondName,
            String password,
            Long personNumber,
            CityEntity currentCity
    ) {
        this.id = id;
        this.name = name;
        this.secondName = secondName;
        this.password = password;
        this.personNumber = personNumber;
        this.currentCity = currentCity;
    }

    public DriverEntity() {}

}
