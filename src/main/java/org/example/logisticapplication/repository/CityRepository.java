package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.City.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {
    boolean existsCityEntityByName(String name);
}
