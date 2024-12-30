package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryMapRepository extends JpaRepository<CountryMapEntity, Long> {

    @EntityGraph(attributePaths = {"cities"})
    @Query("SELECT c FROM CountryMapEntity c LEFT JOIN FETCH c.distances")
    List<CountryMapEntity> findAllMaps();

    boolean existsCountryMapEntitiesByCountryName(String countryName);

    @Query("SELECT COUNT(c) > 0 FROM CountryMapEntity c WHERE c.id =:countryId")
    boolean existsCountryMapEntitiesById(@Param("countryId") Long countryId);

    @EntityGraph(value = "country_map_with_cities")
    @Query("SELECT c FROM CountryMapEntity c WHERE c.id =:countryId")
    Optional<CountryMapEntity> findById(
            @Param("countryId") Long countryId
    );

    @Query("SELECT ct FROM CountryMapEntity ct LEFT JOIN CityEntity ce ON ce.countryMap.id = ct.id WHERE ce.name =:cityName")
    Optional<CountryMapEntity> findByCityName(@Param("cityName")String cityName);

}