package org.example.logisticapplication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.utils.UserParamDto;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.RegistrationRepository;
import org.example.logisticapplication.web.IncorrectUserDataForLogin;
import org.example.logisticapplication.web.UserNotFoundExecution;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRepository registrationRepository;
    private static final String GREETING = "Welcome!";
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public HttpStatus checkUserData(
            UserParamDto userParamDto
    ) {
        return driverRepository.findDriverEntityByPersonNumber(userParamDto.userNumber())
                .filter(driver -> passwordEncoder.matches(driver.getName(), userParamDto.userName()))
                .map(driver -> HttpStatus.OK)
                .orElse(HttpStatus.UNAUTHORIZED);

    }

    @Transactional
    public Long getDriverIdByUserNumber(Long number) {
        return driverRepository.findDriverEntityByPersonNumber(number).orElseThrow(
                () -> new UserNotFoundExecution("User with number=%s not found"
                        .formatted(number))
        ).getId();
    }
}
