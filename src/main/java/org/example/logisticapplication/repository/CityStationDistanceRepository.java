package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.CityStationDistance.CityStationDistanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityStationDistanceRepository extends JpaRepository<CityStationDistanceEntity, Long> {
    @Query("""
            SELECT DISTINCT std 
            FROM CityStationDistanceEntity std
            WHERE (std.stationFrom.id IN (:ids) AND std.stationTo.id IN (:ids))
            """)
    CityStationDistanceEntity findDistancesByStationsIds(
            @Param("ids") List<Long> cityStationIds
    );

    @Query("""
            SELECT (COUNT(sd) > 0)
            FROM CityStationDistanceEntity sd
            WHERE (sd.stationFrom.name IN(:names) AND sd.stationTo.name IN(:names))                        
            """)
    boolean isExistsDistanceByStationNames(
            @Param("names") List<String> names
    );
}
