package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CargoRepository extends JpaRepository<CargoEntity, Long> {

    boolean existsCargoEntitiesByNumber(String number);

    @Modifying
    @Query(value = """
            UPDATE cargo c
            SET status = :newStatus
            WHERE c.id = :cargoId
              AND EXISTS (SELECT 1
                          FROM driver_order dor
                                   INNER JOIN driver d ON dor.driver_id = d.id
                                   INNER JOIN route_point rp ON rp.order_id = dor.order_id
                                   INNER JOIN route_point_cargo rop ON rp.id = rop.route_point_id
                          WHERE d.id = :driverId
                            AND dor.order_id = :orderId
                            AND c.id = rop.cargo_id)
            """, nativeQuery = true)
    void updateCargoEntityStatus(
            @Param("newStatus") String status,
            @Param("cargoId") Long cargoId,
            @Param("driverId") Long driverId,
            @Param("orderId") Long orderId
    );

}
