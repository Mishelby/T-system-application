package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.StationDistance.StationDistanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationDistanceRepository extends JpaRepository<StationDistanceEntity, Long> {

    @Query("""
            SELECT DISTINCT std 
            FROM StationDistanceEntity std
            WHERE (std.stationFrom.id IN (:ids) AND std.stationTo.id IN (:ids))
            """)
    StationDistanceEntity findDistancesByStationsIds(
            @Param("ids")List<Long> cityStationIds
    );
}
