package com.omgsrt.Ludolify.v1.account;

import com.omgsrt.Ludolify.shared.dataInitializer.DataInitializer;
import com.omgsrt.Ludolify.v1.role.Role;
import com.omgsrt.Ludolify.v1.role.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Order(2)
public class AccountInitializer implements DataInitializer {
    AccountRepository accountRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public Mono<Void> initialize() {
        return accountRepository.findByUsernameContainingIgnoreCase("ADMIN")
                .hasElement()
                .flatMap(exists -> {
                    if (!exists) {
                        return roleRepository.findByNameContainingIgnoreCase("ADMINISTRATOR")
                                .map(Role::getId)
                                .flatMap(adminRoleId -> {
                                    log.info("Creating admin account...");
                                    Account admin = Account.builder()
                                            .username("ADMIN")
                                            .handle("@ludolify_admin")
                                            .dateOfBirth(new Date())
                                            .bio("Dev Team At Ludolify")
                                            .status(AccountStatus.ACTIVE)
                                            .email("personal_email@gmail.com")
                                            .phone("0123456789")
                                            .password(passwordEncoder.encode("adminludolify"))
                                            .roleIds(Set.of(adminRoleId))
                                            .createdAt(new Date())
                                            .updatedAt(new Date())
                                            .build();
                                    return accountRepository.save(admin);
                                }).then();
                    }
                    return Mono.empty();
                });
    }
}
