package org.example.logisticapplication.domain.CountryMap;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Distance.DistanceEntity;

import java.util.Set;

@Entity
@Table(name = "country_map")
@Getter
@Setter
@NamedEntityGraph(
        name = "country_map_with_cities",
        attributeNodes = {
                @NamedAttributeNode("cities")
        }
)
public class CountryMapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_name", nullable = false, unique = true)
    private String countryName;

    @OneToMany(mappedBy = "countryMap", fetch = FetchType.LAZY)
    private Set<CityEntity> cities;

    @OneToMany(mappedBy = "countryMap", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<DistanceEntity> distances;


    public CountryMapEntity(
            Long id,
            String countryName,
            Set<CityEntity> cities,
            Set<DistanceEntity> distances
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