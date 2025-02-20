package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Driver.DriverInfoForUserDto;
import org.example.logisticapplication.domain.DriverOrderEntity.DriverOrderEntity;
import org.example.logisticapplication.domain.Order.OrderEntity;
import org.example.logisticapplication.domain.User.*;
import org.example.logisticapplication.domain.UserOrders.UserOrderEntity;
import org.example.logisticapplication.mapper.UserMapper;
import org.example.logisticapplication.mapper.UserOrderMapper;
import org.example.logisticapplication.repository.DriverRepository;
import org.example.logisticapplication.repository.UserOrderInfoRepository;
import org.example.logisticapplication.repository.UserOrderRepository;
import org.example.logisticapplication.repository.UserRepository;
import org.example.logisticapplication.utils.RoleName;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserOrderRepository userOrderRepository;
    private final UserOrderMapper userOrderMapper;
    private final DriverRepository driverRepository;
    private final UserOrderInfoRepository userOrderInfoRepository;

    @Transactional
    public CreateUserDto createNewUser(
            CreateUserDto newUser
    ) {
        isUserExistsByEmail(newUser.email());
        isUserExistsByUserName(newUser.username());

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
    public UserInfo getUserInfo(
            Long id
    ) {
        var userEntity = getUserEntity(id);
        var userOrders = userOrderRepository.findByUserId(id);

        var userOrderInfo = userOrders.stream()
                .map(userOrder -> {
                    var order = userOrder.getOrder();
                    var drivers = driverRepository.findAllDriversForOrder(order.getId());
                    var desiredDateForOrder = userOrderInfoRepository.findDesiredDateForOrder(order.getUniqueNumber());
                    var driversInfo = getDriversInfo(drivers);

                    return userOrderMapper.toInfoDto(
                            order,
                            new DriverInfoForUserDto(
                                    driversInfo,
                                    desiredDateForOrder
                            )
                    );
                })
                .toList();

        return getUserInfo(userOrders, userOrderInfo, userEntity);
    }

    private static Map<List<String>, List<Long>> getDriversInfo(
            List<DriverEntity> drivers
    ) {
        return drivers.stream()
                .collect(Collectors.groupingBy(
                        driver -> List.of(driver.getName()),
                        Collectors.mapping(
                                DriverEntity::getPersonNumber,
                                Collectors.toList()
                        )));
    }

    private UserInfo getUserInfo(
            List<UserOrderEntity> userOrders,
            List<UserOrderInfo> orders,
            UserEntity userEntity
    ) {
        return userOrders.isEmpty()
                ? userMapper.toMainInfoWithoutOrders(userEntity)
                : userMapper.toInfoWithOrdersAndDrivers(userEntity, orders);
    }

    private void isUserExistsByEmail(
            String email
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email = %s already exists"
                    .formatted(email));
        }
    }

    private void isUserExistsByUserName(String username) {
        boolean isExists = userRepository.existsByUserName(username);

        if (isExists) {
            throw new IllegalArgumentException("User with username = %s already exists"
                    .formatted(username));
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

    private UserEntity getUserEntity(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id = %s not found"
                        .formatted(id))
        );
    }

}
