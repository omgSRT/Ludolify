package com.omgsrt.Ludolify.shared.account;

import com.omgsrt.Ludolify.shared.account.dto.request.LoginRequest;
import com.omgsrt.Ludolify.shared.account.dto.response.LoginResponse;
import com.omgsrt.Ludolify.shared.exception.AppException;
import com.omgsrt.Ludolify.shared.exception.ErrorCode;
import com.omgsrt.Ludolify.shared.role.Role;
import com.omgsrt.Ludolify.shared.role.RoleRepository;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Value("${spring.jwt.secret}")
    @NonFinal
    String jwtSecret;
    @NonFinal
    SecretKey secretKey;
    @NonFinal
    JwtParser jwtParser;

    AccountRepository accountRepository;
    RoleRepository roleRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts.parser().verifyWith(this.secretKey).build();
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(16);

        return accountRepository.findByUsernameContainingIgnoreCase(request.getUsername())
                .flatMap(account -> {
                    boolean isPasswordMatched = passwordEncoder.matches(request.getPassword(), account.getPassword());
                    if (!isPasswordMatched) {
                        throw new AppException(ErrorCode.INVALID_USERNAME_OR_PASSWORD);
                    }

                    var accessToken = generateToken(account, 1);
                    var refreshToken = generateToken(account, 365);

                    return Mono.zip(accessToken, refreshToken)
                            .map(tuple -> new LoginResponse(tuple.getT1(), tuple.getT2()));
                })
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_USERNAME_OR_PASSWORD)));
    }

    public Mono<String> generateToken(Account account, int expirationDay) {
        Date now = new Date();
        Instant nowInstant = now.toInstant();
        Instant expirationInstant = nowInstant.plus(expirationDay, ChronoUnit.DAYS);
        Date expirationTime = Date.from(expirationInstant);

        return roleRepository.findAllById(account.getRoleIds())
                .map(Role::getName)
                .collectList()
                .map(roleNameList -> Jwts.builder()
                        .subject(account.getUsername())
                        .issuer("dev-ludolify")
                        .issuedAt(now)
                        .expiration(expirationTime)
                        .id(UUID.randomUUID().toString())
                        .claim("accountId", account.getId().toString())
                        .claim("roles", roleNameList)
                        .signWith(secretKey)
                        .compact());
    }
}
