package org.example.logisticapplication.domain.City;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.example.logisticapplication.domain.Distance.DistanceEntity;


import java.util.List;

@Entity
@Table(name = "city")
@Getter
@Setter
public class CityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_map_id", nullable = false)
    private CountryMapEntity countryMap;

    public CityEntity(
            Long id,
            String name,
            CountryMapEntity countryMap
    ) {
        this.id = id;
        this.name = name;
        this.countryMap = countryMap;
    }

    public CityEntity(
            String name,
            CountryMapEntity countryMap
    ) {
        this.name = name;
        this.countryMap = countryMap;
    }

    public CityEntity() {}
}
