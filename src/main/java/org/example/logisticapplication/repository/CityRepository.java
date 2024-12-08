package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.City.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {
    boolean existsCityEntityByName(String name);

    @Query(value = """
            SELECT c.id, c.id, c.name FROM city c
            WHERE c.id IN (:cityIds)
            AND c.country_map_id = :countryId
            """,
            nativeQuery = true)
    List<CityEntity> findCitiesByIds(
            @Param("cityIds") List<Long> cityIds,
            @Param("countryId")Long countryId
    );

    @Query("SELECT COUNT(d) > 0 FROM DistanceEntity d WHERE d.fromCity = :fromCity AND d.toCity = :toCity")
    boolean existsByCities(
            @Param("fromCity") CityEntity fromCity,
            @Param("toCity") CityEntity toCity
    );

}
