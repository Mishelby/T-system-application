package org.example.logisticapplication.domain.CityStationDistance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.logisticapplication.domain.CityStationEntity.CityStationEntity;
import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;

@Entity
@Table(name = "station_distance")
@Getter
@Setter
public class CityStationDistanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_station_id")
    private CityStationEntity stationFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_station_id")
    private CityStationEntity stationTo;

    @Column(name = "distance")
    private Double distance;

    @ManyToOne
    @JoinColumn(name = "country_map_id", nullable = false)
    private CountryMapEntity countryMap;

    public CityStationDistanceEntity(
            CityStationEntity stationFrom,
            CityStationEntity stationTo,
            Double distance,
            CountryMapEntity countryMap
    ) {
        this.stationFrom = stationFrom;
        this.stationTo = stationTo;
        this.distance = distance;
        this.countryMap = countryMap;
    }

    public CityStationDistanceEntity() {}
}
