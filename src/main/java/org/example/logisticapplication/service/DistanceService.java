package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.repository.DistanceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistanceService {
    private final DistanceRepository distanceRepository;
}
