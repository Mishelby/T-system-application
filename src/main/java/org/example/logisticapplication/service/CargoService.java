package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Cargo.Cargo;
import org.example.logisticapplication.repository.CargoRepository;
import org.example.logisticapplication.utils.CargoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CargoService {
    private final CargoRepository cargoRepository;
    private final CargoMapper cargoMapper;

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

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteCargo(
            Long id
    ) {
        if(!cargoRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cargo with id=%s not found"
                            .formatted(id)
            );
        }

        cargoRepository.deleteById(id);
    }
}
