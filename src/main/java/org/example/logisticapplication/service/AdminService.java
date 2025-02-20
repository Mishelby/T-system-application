package org.example.logisticapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.Admin.AdminEntity;
import org.example.logisticapplication.domain.Admin.BaseAdminDto;
import org.example.logisticapplication.domain.Admin.CreateAdminDto;
import org.example.logisticapplication.domain.Role.RoleEntity;
import org.example.logisticapplication.mapper.AdminMapper;
import org.example.logisticapplication.repository.AdminRepository;
import org.example.logisticapplication.utils.RoleName;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Transactional
    public BaseAdminDto createAdmin(
            CreateAdminDto createAdminDto
    ) {
        userService.isUserExistsByEmail(createAdminDto.email());
        userService.isUserExistsByUserName(createAdminDto.username());
        var allRoles = roleService.getAllRoles();

        var entity = adminMapper.toEntity(
                createAdminDto,
                passwordEncoder.encode(createAdminDto.password()),
                Set.of(allRoles.get(RoleName.ADMIN))
        );

        log.info("Admin Entity after mapping {}", entity);

        return adminMapper.toBaseDto(
                adminRepository.save(entity)
        );
    }

    @Transactional(readOnly = true)
    public Long getAdminIdByEmail(String email) {
        return adminRepository.findByEmail(email)
                .map(AdminEntity::getId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Admin with email %snot found"
                                .formatted(email)
                ));
    }

    @Transactional(readOnly = true)
    public BaseAdminDto getAdminInfoById(Long id) {
        return adminMapper.toBaseDto(adminRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Admin with id = %s not found"
                        .formatted(id))
        ));
    }
}
