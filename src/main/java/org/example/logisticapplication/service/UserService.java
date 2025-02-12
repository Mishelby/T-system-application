package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.User.CreateUserDto;
import org.example.logisticapplication.domain.User.MainUserInfoDro;
import org.example.logisticapplication.mapper.UserMapper;
import org.example.logisticapplication.repository.UserRepository;
import org.example.logisticapplication.utils.RoleName;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserService  {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Transactional
    public CreateUserDto createNewUser(
            CreateUserDto newUser
    ) {
        isUserExistsByEmail(newUser.email());
        var roles = roleService.findAll();

        var userEntity = userMapper.toEntity(
                newUser,
                passwordEncoder.encode(newUser.password()),
                Set.of(roles.get(RoleName.USER))
        );

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

        return userMapper.toMainInfo(userEntity);
    }

    @Transactional(readOnly = true)
    public MainUserInfoDro getUserInfo(
            String email
    ) {
        var userEntity = userRepository.findUserIdEntityByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User with email = %s not found"
                        .formatted(email))
        );

        return userMapper.toMainInfo(userEntity);
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
