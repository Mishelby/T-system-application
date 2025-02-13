package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.CityStationEntity.CityStationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


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
}
