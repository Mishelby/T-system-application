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

    @Query("SELECT c FROM CityEntity c WHERE c.id IN (:cityIds)")
    List<CityEntity> findCitiesByIds(@Param("cityIds") List<Long> cityIds);

    @Query("SELECT COUNT(d) > 0 FROM DistanceEntity d WHERE d.fromCity = :fromCity AND d.toCity = :toCity")
    boolean existsByCities(
            @Param("fromCity") CityEntity fromCity,
            @Param("toCity") CityEntity toCity
    );

}
