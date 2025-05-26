package com.pnu.ailifelog.config;

import com.pnu.ailifelog.entity.Role;
import com.pnu.ailifelog.entity.RoleName;
import com.pnu.ailifelog.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }

    private void initializeRoles() {
        // ROLE_USER 초기화
        if (roleRepository.findByName(RoleName.ROLE_USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(RoleName.ROLE_USER);
            roleRepository.save(userRole);
            log.info("ROLE_USER 역할이 생성되었습니다.");
        }

        // ROLE_ADMIN 초기화
        if (roleRepository.findByName(RoleName.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);
            log.info("ROLE_ADMIN 역할이 생성되었습니다.");
        }
    }
} 