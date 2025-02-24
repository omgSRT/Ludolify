package com.omgsrt.Ludolify.v1.role;

import com.omgsrt.Ludolify.shared.dataInitializer.DataInitializer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Order(1)
public class RoleInitializer implements DataInitializer {
    RoleRepository roleRepository;
    private static final Set<String> DEFAULT_ROLES = Set.of(
            "ADMINISTRATOR",
            "MODERATOR",
            "MEMBER"
    );

    @Override
    public Mono<Void> initialize() {
        return roleRepository.findAll()
                .map(Role::getName)
                .collect(Collectors.toSet()) // Convert to Set of existing role names
                .flatMap(existingRoles -> {
                    Set<String> missingRoles = DEFAULT_ROLES.stream()
                            .filter(role -> !existingRoles.contains(role))
                            .collect(Collectors.toSet());

                    if (missingRoles.isEmpty()) {
                        log.info("All roles already exist. No new roles added.");
                        return Mono.empty();
                    }

                    log.info("Adding missing roles: {}", missingRoles);

                    Set<Role> newRoles = missingRoles.stream()
                            .map(name -> Role.builder()
                                    .name(name)
                                    .createdAt(new Date())
                                    .updatedAt(new Date())
                                    .build())
                            .collect(Collectors.toSet());

                    return roleRepository.saveAll(newRoles).then();
                });
    }
}
