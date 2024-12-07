package org.example.logisticapplication.domain.CountryMap;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Distance.DistanceEntity;

import java.util.List;

@Entity
@Table(name = "country_map")
@Getter
@Setter
public class CountryMapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_name", nullable = false, unique = true)
    private String countryName;

    @OneToMany(mappedBy = "countryMap", fetch = FetchType.LAZY)
    private List<CityEntity> cities;

    @OneToMany(mappedBy = "countryMap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DistanceEntity> distances;

    public CountryMapEntity(
            Long id,
            String countryName,
            List<CityEntity> cities,
            List<DistanceEntity> distances
    ) {
        this.id = id;
        this.countryName = countryName;
        this.cities = cities;
        this.distances = distances;
    }

    public CountryMapEntity(
            String countryName
    ) {
        this.countryName = countryName;
    }

    public CountryMapEntity() {}
}