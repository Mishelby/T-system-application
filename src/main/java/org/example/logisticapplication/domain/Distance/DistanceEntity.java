package org.example.logisticapplication.domain.Distance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;

@Entity
@Table(name = "distances")
@Getter
@Setter
public class DistanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_city_id", nullable = false)
    private CityEntity fromCity;

    @ManyToOne
    @JoinColumn(name = "to_city_id", nullable = false)
    private CityEntity toCity;

    @Column(name = "distance")
    private Double  distance;

    @ManyToOne
    @JoinColumn(name = "country_map_id", nullable = false)
    private CountryMapEntity countryMap;


    public DistanceEntity(
            Long id,
            CityEntity fromCity,
            CityEntity toCity,
            Double  distance
    ) {
        this.id = id;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.distance = distance;
    }

    public DistanceEntity() {}
}
