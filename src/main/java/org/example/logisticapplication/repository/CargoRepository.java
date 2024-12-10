package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CargoRepository extends JpaRepository<CargoEntity, Long> {

    boolean existsCargoEntitiesByNumber(String number);

}
