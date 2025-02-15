package org.example.logisticapplication.repository;

import org.example.logisticapplication.domain.Cargo.CargoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


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

    @Query("""
            SELECT c
            FROM CargoEntity c
            WHERE c.number = :number  
            """)
    CargoEntity findCargoByNumber(String number);

    @Query(value = """
            SELECT c
            FROM CargoEntity c
            LEFT JOIN OrderCargoEntity oc ON c.id = oc.cargo.id
            WHERE oc.order.uniqueNumber = :orderNumber
            """)
    List<CargoEntity> findCargosForOrder(
            @Param("orderNumber") String orderNumber
    );

    @Query("""
            SELECT oc.cargo
            FROM OrderCargoEntity oc
            LEFT JOIN CargoEntity ce ON oc.cargo.id = ce.id
            WHERE oc.order.uniqueNumber = :number            
            """)
    List<CargoEntity> findOrderCargoByOrderNumber(
            @Param("number") String orderNumber
    );

    @Modifying
    @Query("""
            UPDATE CargoEntity c 
            SET c.status = :status
            WHERE c.number = :number
            """)
    int updateCargoStatusByNumber(String number, String status);
}
