package org.example.logisticapplication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.controllers.rest.UserParamDto;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.RegistrationRepository;
import org.example.logisticapplication.web.UserNotFoundExecution;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private static final String GREETING = "Welcome %s!";
    private final DriverRepository driverRepository;

    @Transactional
    public boolean checkUserData(
            UserParamDto userParamDto
    ) {
        return registrationRepository.checkDriverInSystem(
                userParamDto.userName(),
                userParamDto.userNumber()
        );
    }

    @Transactional
    public Long getDriverIdByUserNumber(Long number) {
        return driverRepository.findDriverEntityByPersonNumber(number).orElseThrow(
                () -> new UserNotFoundExecution("User with number=%s not found"
                        .formatted(number))
        ).getId();
    }
}
