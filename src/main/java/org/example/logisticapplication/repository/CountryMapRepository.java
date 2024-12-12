package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.CountryMap.CountryMapEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryMapRepository extends JpaRepository<CountryMapEntity, Long> {
    boolean existsCountryMapEntitiesByCountryName(String countryName);

    @Query("SELECT COUNT(c) > 0 FROM CountryMapEntity c WHERE c.id =:countryId")
    boolean existsCountryMapEntitiesById(@Param("countryId") Long countryId);

    @Query("SELECT c FROM CountryMapEntity c WHERE c.id =:countryId")
    @EntityGraph(value = "country_map_with_cities")
    Optional<CountryMapEntity> findById(
            @Param("countryId") Long countryId
    );

}
