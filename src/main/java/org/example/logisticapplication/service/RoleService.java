package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Role.Role;
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

    @Transactional(readOnly = true)
    public ConcurrentMap<RoleName, Role> findAll() {
        var allRoles = roleRepository.findAll();

        if(allRoles.size() < RoleName.values().length) {
            Arrays.stream(RoleName.values())
                    .filter(roleName -> allRoles.stream().noneMatch(role -> role.getName().equals(roleName)))
                    .forEach(roleName -> allRoles.add(roleRepository.save(new Role(roleName))));
        }

        return  allRoles.stream()
                .collect(Collectors.toMap(
                        Role::getName,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        ConcurrentHashMap::new
                ));
    }
}
