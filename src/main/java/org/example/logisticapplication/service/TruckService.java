package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Truck.*;
import org.example.logisticapplication.repository.CityRepository;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.TruckRepository;
import org.example.logisticapplication.mapper.TruckMapper;
import org.example.logisticapplication.utils.GenerateNumberForTruck;
import org.example.logisticapplication.utils.TruckValidHelper;
import org.example.logisticapplication.web.CityNotFoundException;
import org.example.logisticapplication.web.TruckAlreadyExists;
import org.example.logisticapplication.web.TruckDeletionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TruckService {
    private final TruckRepository truckRepository;
    private final TruckMapper truckMapper;
    private final DriverRepository driverRepository;
    private final CityRepository cityRepository;

    @Transactional
    public Truck createNewTruck(
            CreateTruckDto truck
    ) {
        var registrationNumber = GenerateNumberForTruck.generateNumberForTruck();

        if (truckRepository.existsByRegistrationNumber(registrationNumber)) {
            throw new TruckAlreadyExists("Truck with registration number %s already exists"
                    .formatted(registrationNumber)
            );
        }

        var cityEntity = getCityEntity(truck);

        var savedTruck = truckRepository.save(
                truckMapper.toEntity(
                        truck,
                        registrationNumber,
                        cityEntity
                )
        );

        return truckMapper.toDomain(savedTruck);
    }

    @Transactional(readOnly = true)
    public Truck findById(
            Long id
    ) {
        var truckEntity = truckRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(
                        TruckValidHelper.getDEFAULT_MESSAGE().formatted(id)
                )
        );

        return truckMapper.toDomain(truckEntity);
    }

    @Transactional(readOnly = true)
    public List<Truck> findAll() {
        var allTrucks = truckRepository.findAll();

        if (allTrucks.isEmpty()) {
            return new ArrayList<>();
        }

        return allTrucks
                .stream()
                .map(truckMapper::toDomain)
                .toList();
    }

    @Transactional
    public void deleteTruck(
            Long id
    ) {
        var truckEntity = truckRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(
                        TruckValidHelper.getDEFAULT_MESSAGE().formatted(id)
                )
        );

        if (!driverRepository.isTruckInOrder(truckEntity)) {
            throw new TruckDeletionException("Truck with id = %s is currently assigned to an order"
                    .formatted(id));
        }

        driverRepository.removeCurrentTruck(truckEntity);

        truckRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Truck updateTruck(
            Long id,
            Truck updateTruck
    ) {

        if (!truckRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    TruckValidHelper.getDEFAULT_MESSAGE().formatted(id)
            );
        }

        truckRepository.updateTruckById(
                id,
                updateTruck.registrationNumber(),
                updateTruck.driversShift(),
                updateTruck.status(),
                updateTruck.capacity(),
                updateTruck.currentCityId()
        );

        return truckMapper.toDomain(
                truckRepository.findById(id).orElseThrow()
        );
    }

    @Transactional(readOnly = true)
    public List<Truck> findFreeTrucks(
            Long cityId
    ) {
        var freeTrucks = truckRepository.findFreeTrucks(
                cityId,
                TruckStatus.SERVICEABLE.name()
        );

        if (freeTrucks.isEmpty()) {
            return new ArrayList<>();
        }

        return freeTrucks.stream()
                .map(truckMapper::toDomain)
                .toList();
    }

    public List<TruckForDriverDto> findTruckForDriver(
            Long driverId
    ) {
        var driverEntity = driverRepository.findById(driverId).orElseThrow();

        var truckForDriver = truckRepository.findTruckForDriver(
                driverEntity.getCurrentCity().getId(),
                TruckStatus.SERVICEABLE.name()
        );

        if (truckForDriver.isEmpty()) {
            return Collections.emptyList();
        }

        return truckForDriver.stream()
                .map(truckMapper::toDto)
                .toList();
    }

    private CityEntity getCityEntity(CreateTruckDto truck) {
        return cityRepository.findCityEntityByName(truck.currentCityName()).orElseThrow(
                () -> new CityNotFoundException("City with name %s not found"
                        .formatted(truck.currentCityName()))
        );
    }
}
