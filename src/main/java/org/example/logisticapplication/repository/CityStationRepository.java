package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.CityStationEntity.CityStationEntity;
import org.example.logisticapplication.domain.StationDistance.StationDistanceEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CityStationRepository extends JpaRepository<CityStationEntity, Long> {

    @Query("""
            SELECT (COUNT(st) > 0) 
            FROM CityStationEntity st
            WHERE st.name = :name 
            AND st.city.name = :cityName                                  
            """)
    boolean findCityStationEntitiesByName(
            @Param("name") String name,
            @Param("cityName") String cityName
    );

    @Query("""
            SELECT st
            FROM StationDistanceEntity st
            WHERE st.stationFrom.id IN (:ids) 
            AND st.stationTo.id IN (:ids)
            """)
    Optional<StationDistanceEntity> findDistanceByIds(
            @Param("ids") List<Long> ids
    );

    @EntityGraph(attributePaths = {"city"})
    @Query("""
            SELECT cse
            FROM CityStationEntity cse
            WHERE cse.city.name IN (:cityNames)
            """)
    List<CityStationEntity> findStationByCityNames(List<String> cityNames);
}
