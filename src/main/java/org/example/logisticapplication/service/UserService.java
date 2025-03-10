package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Driver.DriverInfoForUserDto;
import org.example.logisticapplication.domain.OrderProfileInfo.UserProfileInfoEntity;
import org.example.logisticapplication.domain.User.*;
import org.example.logisticapplication.domain.UserOrders.UserOrderEntity;
import org.example.logisticapplication.mapper.UserMapper;
import org.example.logisticapplication.mapper.UserOrderMapper;
import org.example.logisticapplication.mapper.UserProfileMapper;
import org.example.logisticapplication.repository.*;
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
    private final UserProfileInfoRepository userProfileInfoRepository;
    private final UserProfileMapper userProfileMapper;

    @Transactional
    public CreateUserDto createNewUser(
            CreateUserDto newUser
    ) {
        isUserExistsByEmail(newUser.email());
        isUserExistsByUserName(newUser.username());

        var roles = roleService.getAllRoles();

        var userEntity = userMapper.toEntity(
                newUser,
                passwordEncoder.encode(newUser.password()),
                Set.of(roles.get(RoleName.USER))
        );

        return userMapper.toDto(
                userRepository.save(userEntity)
        );
    }

    @Transactional
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

        var savedUserProfile = getUserProfileInfoEntity(userEntity);

        return getUserInfo(userOrders, userOrderInfo, savedUserProfile.getUser());
    }

    private UserProfileInfoEntity getUserProfileInfoEntity(
            UserEntity userEntity
    ) {
        boolean isExists = userProfileInfoRepository.existsById(userEntity.getId());

        if (!isExists) {
            return userProfileInfoRepository.save(
                    userProfileMapper.toEntity(userEntity)
            );
        }

        return userProfileInfoRepository.getUserProfileInfoById(userEntity.getId())
                .orElseThrow(EntityNotFoundException::new);
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

    public void isUserExistsByEmail(
            String email
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email = %s already exists"
                    .formatted(email));
        }
    }

    public void isUserExistsByUserName(String username) {
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
