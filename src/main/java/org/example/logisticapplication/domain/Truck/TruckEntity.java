package org.example.logisticapplication.domain.Truck;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.DriverEntity;

import java.util.List;

@Entity
@Table(name = "trucks")
@Getter
@Setter
public class TruckEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reg_number",unique = true, nullable = false)
    private String registrationNumber;

    @Column(name = "drivers_shift")
    private Integer driversShift;

    @Column(name = "status")
    private String status;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private CityEntity currentCity;

    @Column(name = "capacity")
    private Double capacity;

    @OneToMany(mappedBy = "currentTruck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DriverEntity> drivers;

    public TruckEntity(
            Long id,
            String registrationNumber,
            Integer driversShift,
            String status,
            Double capacity,
            CityEntity currentCity
    ) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.driversShift = driversShift;
        this.status = status;
        this.capacity = capacity;
        this.currentCity = currentCity;
    }

    public TruckEntity() {}
}
