package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.controllers.rest.LoginResponse;
import org.example.logisticapplication.domain.User.LoginUserDto;
import org.example.logisticapplication.repository.UserRepository;
import org.example.logisticapplication.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public LoginResponse authenticate(LoginUserDto loginUserDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserDto.username(), loginUserDto.password())
            );
            log.info("Аутентификация прошла успешно.");
        } catch (BadCredentialsException e) {
            log.error("Ошибка аутентификации: неверные данные.");
            throw new UsernameNotFoundException("Неверные данные для аутентификации.");
        }

        var userEntity = userRepository.findByName(loginUserDto.username())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь с именем %s не найден!".formatted(loginUserDto.username()))
                );

        return new LoginResponse(jwtTokenProvider.generateToken(userEntity), userEntity.getId());
    }

}
