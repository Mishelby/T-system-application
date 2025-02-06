package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Cargo.*;
import org.example.logisticapplication.repository.CargoRepository;
import org.example.logisticapplication.mapper.CargoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CargoService {
    private final CargoRepository cargoRepository;
    private final CargoMapper cargoMapper;
    private final JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Cargo createNewCargo(
            Cargo cargo
    ) {
        if (cargoRepository.existsCargoEntitiesByNumber(cargo.number())) {
            throw new IllegalArgumentException(
                    "Cargo number=%s already exists"
                            .formatted(cargo.number())
            );
        }

        var savedCargo = cargoRepository.save(
                cargoMapper.toEntity(cargo)
        );

        return cargoMapper.toDomain(savedCargo);
    }

    @Transactional(readOnly = true)
    public Cargo findCargoById(
            Long id
    ) {
        var cargoEntity = cargoRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Cargo with id=%s not found"
                                .formatted(id)
                )
        );

        return cargoMapper.toDomain(cargoEntity);

    }

    @Transactional
    public HttpStatus updateCargoStatus(
            CargoStatusDto cargoInfo
    ) {
        boolean valid = CargoStatus.isValid(cargoInfo.status());

        if (!valid) {
            throw new IllegalArgumentException("Invalid status = %s for Cargo =%s"
                    .formatted(cargoInfo.status(), cargoInfo.number()));
        }

        int count = cargoRepository.updateCargoStatusByNumber(
                cargoInfo.number(),
                cargoInfo.status()
        );

        return count > 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND;
    }

    @Transactional(readOnly = true)
    public List<CargoInfoForStatus> findCargosForOrder(
            String orderNumber
    ) {
        var cargosForOrder = cargoRepository.findCargosForOrder("ORDER-ncnz7uf5fc");
        log.info("CargosForOrder={}", cargosForOrder);

        return cargosForOrder.stream()
                .map(cargoMapper::toInfoStatus)
                .toList();
    }

    @Transactional
    public void deleteCargo(
            Long id
    ) {
        if (!cargoRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cargo with id=%s not found"
                            .formatted(id)
            );
        }


        cargoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CargoStatusDto> findCargoStatusById(
            Long id
    ) {
        if (!cargoRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Order with id=%s not found"
                            .formatted(id)
            );
        }

        /**
         * Request for the number and status of cargo
         * Return new CargoStatusDto(String number, String status)
         */
        String sql = """
                SELECT c.number, c.status
                    FROM orders o
                        JOIN route_point rp ON rp.order_id = o.id
                        JOIN route_point_cargo rpc ON rpc.route_point_id = rp.id
                        JOIN cargo c ON c.id = rpc.cargo_id
                WHERE o.id = ?
                GROUP BY c.number, c.status;
                """;

        return jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) ->
                new CargoStatusDto(rs.getString("number"), rs.getString("status")));
    }
}
