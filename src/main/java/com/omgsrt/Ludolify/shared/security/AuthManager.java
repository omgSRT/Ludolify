package com.omgsrt.Ludolify.shared.security;

import com.omgsrt.Ludolify.shared.exception.AppException;
import com.omgsrt.Ludolify.shared.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Component
public class AuthManager implements ReactiveAuthenticationManager {
    SpringSecurityService springSecurityService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("AuthManager: Processing authentication: {}", authentication);
        return Mono.justOrEmpty(authentication)
                .cast(BearerToken.class)
                .switchIfEmpty(Mono.just(new BearerToken("No token provided")))
                .flatMap(auth -> {
                    String token = auth.getCredentials();
                    log.info("AuthManager: Processing token: {}", token);
                    String subject = springSecurityService.getSubject(auth.getCredentials());
                    List<String> roles = springSecurityService.getRolesFromToken(auth.getCredentials());
                    log.info("AuthManager: Subject: {}, Roles: {}", subject, roles);
                    return springSecurityService.getAccountByUsername(subject)
                            .map(account -> {
                                if (account.getUsername() == null) {
                                    log.error("Account not found for subject: {}", subject);
                                    throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
                                }
                                if (!springSecurityService.validateJwt(account, auth.getCredentials())) {
                                    log.error("Invalid or expired token for subject: {}", subject);
                                    throw new AppException(ErrorCode.INVALID_OR_EXPIRED_TOKEN);
                                }

                                List<SimpleGrantedAuthority> authorities = roles.stream()
                                        .map(role -> new SimpleGrantedAuthority(role.toUpperCase()))
                                        .collect(Collectors.toList());
                                log.info("AuthManager: Authorities assigned: {}", authorities);

                                return new UsernamePasswordAuthenticationToken(
                                        account.getUsername(),
                                        account.getPassword(),
                                        authorities
                                );
                            });
                });
    }
}
