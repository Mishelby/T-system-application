package org.example.logisticapplication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.utils.UserParamDto;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.RegistrationRepository;
import org.example.logisticapplication.web.IncorrectUserDataForLogin;
import org.example.logisticapplication.web.UserNotFoundExecution;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private static final String GREETING = "Welcome!";
    private final DriverRepository driverRepository;

    @Transactional
    public String checkUserData(
            UserParamDto userParamDto
    ) {
        var isExists = registrationRepository.checkDriverInSystem(
                userParamDto.userName(),
                userParamDto.userNumber()
        );

        if(!isExists){
            throw new IncorrectUserDataForLogin("Incorrect login or number: %s, %s"
                    .formatted(userParamDto.userName(), userParamDto.userNumber())
            );
        }

        return GREETING.concat(" %s".formatted(userParamDto.userName()));
    }

    @Transactional
    public Long getDriverIdByUserNumber(Long number) {
        return driverRepository.findDriverEntityByPersonNumber(number).orElseThrow(
                () -> new UserNotFoundExecution("User with number=%s not found"
                        .formatted(number))
        ).getId();
    }
}
