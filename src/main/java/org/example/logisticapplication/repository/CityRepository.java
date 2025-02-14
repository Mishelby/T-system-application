package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.City.CityEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {
    @Query("SELECT COUNT(*) > 0 FROM CityEntity c WHERE c.name = :name")
    boolean existsCityEntityByName(@Param("name") String name);

    @Query(value = """
            SELECT * FROM city c
            WHERE c.id IN (:cityIds)
            AND c.country_map_id = :countryId
            """,
            nativeQuery = true)
    List<CityEntity> findCitiesByIds(
            @Param("cityIds") List<Long> cityIds,
            @Param("countryId") Long countryId
    );

    @Query("""
            SELECT c 
            FROM CityEntity c
            LEFT JOIN FETCH CityStationEntity cse ON c.id = cse.city.id
            WHERE c.name = :name
            """)
    Optional<CityEntity> findCityWithStations(
            @Param("name") String cityName
    );

    @Query("SELECT COUNT(d) > 0 FROM DistanceEntity d WHERE d.fromCity = :fromCity AND d.toCity = :toCity")
    boolean existsByCities(
            @Param("fromCity") CityEntity fromCity,
            @Param("toCity") CityEntity toCity
    );

    @Query("SELECT c FROM CityEntity c WHERE c.countryMap.id = :countryId")
    List<CityEntity> findAllByCountryMapId(
            @Param("countryId") Long countryId
    );

    @EntityGraph(attributePaths = {"countryMap"})
    @Query("SELECT c FROM CityEntity c WHERE c.name = :name")
    Optional<CityEntity> findCityEntityByName(String name);

    @Query("""
            SELECT c 
            FROM CityEntity c
            WHERE c.name IN (:names)            
            """)
    List<CityEntity> findCitiesByNames(
            @Param("names") List<String> cityNames
    );
}
