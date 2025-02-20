package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Role.RoleEntity;
import org.example.logisticapplication.repository.RoleRepository;
import org.example.logisticapplication.utils.RoleName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @Transactional
    public synchronized ConcurrentMap<RoleName, RoleEntity> findAll() {
        var allRoles = roleRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                                RoleEntity::getName,
                                Function.identity(),
                                (existing, replacement) -> existing,
                                ConcurrentHashMap::new
                        )
                );

        var ordersStatus = RoleName.values();

        if (allRoles.size() < ordersStatus.length) {
            Arrays.stream(RoleName.values())
                    .filter(roleName -> !allRoles.containsKey(roleName))
                    .forEach(roleName -> {
                        var savedRole = roleRepository.save(new RoleEntity(roleName));
                        allRoles.put(roleName, savedRole);
                    });
        }

        return allRoles;
    }
}
