package org.example.logisticapplication.domain.Distance;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;

@Entity
@Table(name = "distance")
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

    @Column(name = "distance", nullable = false)
    private Long distance;

    @ManyToOne
    @JoinColumn(name = "country_map_id", nullable = false)
    private CountryMapEntity countryMap;

    public DistanceEntity(
            CityEntity fromCity,
            CityEntity toCity,
            Long distance,
            CountryMapEntity countryMap
    ) {
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.distance = distance;
        this.countryMap = countryMap;
    }

    public DistanceEntity() {}

}
