package org.example.logisticapplication.domain.Driver;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.DriverStatus.DriverStatusEntity;
import org.example.logisticapplication.domain.Role.RoleEntity;
import org.example.logisticapplication.domain.ShiftStatus.ShiftStatusEntity;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.example.logisticapplication.domain.User.UserEntity;

import java.util.Set;

@Entity
@Table(name = "driver")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
public class DriverEntity extends UserEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String secondName;

    @Column(name = "person_number", unique = true, nullable = false)
    private Long personNumber;

    @Column(name = "num_of_hours_worked")
    private Double numberOfHoursWorked;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_status_id")
    private DriverStatusEntity driverStatus;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "shift_status_id")
    private ShiftStatusEntity shiftStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_city_id")
    private CityEntity currentCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_truck_id")
    private TruckEntity currentTruck;

    @OneToMany(mappedBy = "driver", orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DriverOrderEntity> driverOrders;

    public DriverEntity(
            String name,
            String secondName,
            Long personNumber,
            CityEntity currentCity,
            TruckEntity currentTruck
    ) {
        this.name = name;
        this.secondName = secondName;
        this.personNumber = personNumber;
        this.currentCity = currentCity;
        this.currentTruck = currentTruck;
    }

    public DriverEntity(
            String userName,
            String password,
            String email,
            Set<RoleEntity> roles,
            String name,
            String secondName,
            Long personNumber,
            CityEntity currentCity
    ) {
        super(userName, password, email, roles);
        this.name = name;
        this.secondName = secondName;
        this.personNumber = personNumber;
        this.currentCity = currentCity;
    }

    public DriverEntity() {
    }

}
