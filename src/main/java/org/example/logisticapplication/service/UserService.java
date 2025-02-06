package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.User.CreateUserDto;
import org.example.logisticapplication.domain.User.MainUserInfoDro;
import org.example.logisticapplication.mapper.UserMapper;
import org.example.logisticapplication.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public CreateUserDto createNewUser(
            CreateUserDto newUser
    ) {
        isUserExistsByEmail(newUser.email());
        var userEntity = userMapper.toEntity(newUser);

        return userMapper.toDto(
                userRepository.save(userEntity)
        );
    }

    @Transactional(readOnly = true)
    public MainUserInfoDro getUserInfo(
            Long id
    ) {
        var userEntity = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id = %s not found"
                        .formatted(id))
        );

        return new MainUserInfoDro(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getCreatedAt().format(DATE_TIME_FORMATTER)
        );
    }

    private void isUserExistsByEmail(
            String email
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email = %s already exists"
                    .formatted(email));
        }
    }

    public Long getUserIdByEmail(
            String email
    ) {
        return userRepository.findUserIdEntityByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email = %s not found"
                        .formatted(email))
        ).getId();
    }
}
