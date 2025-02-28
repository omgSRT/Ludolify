package com.omgsrt.Ludolify.v1.account;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.omgsrt.Ludolify.shared.exception.AppException;
import com.omgsrt.Ludolify.shared.exception.ErrorCode;
import com.omgsrt.Ludolify.v1.account.dto.request.LoginRequest;
import com.omgsrt.Ludolify.v1.account.dto.response.LoginResponse;
import com.omgsrt.Ludolify.v1.role.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    AccountRepository accountRepository;
    RoleRepository roleRepository;

    @Value("${spring.jwt.secret}")
    @NonFinal
    String jwtSecret;

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(16);

        return accountRepository.findByUsernameContainingIgnoreCase(request.getUsername())
                .flatMap(account -> {
                    boolean isPasswordMatched = passwordEncoder.matches(request.getPassword(), account.getPassword());
                    if(!isPasswordMatched){
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
        return buildScope(account)
                .map(scope -> {
                    Date now = new Date();
                    Instant nowInstant = now.toInstant();
                    Instant expirationInstant = nowInstant.plus(expirationDay, ChronoUnit.DAYS);
                    Date expirationTime = Date.from(expirationInstant);

                    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

                    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                            .subject(account.getUsername())
                            .issuer("dev-ludolify")
                            .issueTime(now)
                            .expirationTime(expirationTime)
                            .jwtID(UUID.randomUUID().toString())
                            .claim("accountId", account.getId().toString())
                            .claim("scope", scope)
                            .build();

                    Payload payload = new Payload(jwtClaimsSet.toJSONObject());

                    JWSObject jwsObject = new JWSObject(header, payload);
                    try {
                        jwsObject.sign(new MACSigner(jwtSecret.getBytes()));
                        return jwsObject.serialize();
                    } catch (JOSEException e) {
                        log.error("Cannot Create JWT", e);
                        throw new RuntimeException(e);
                    }
                });
    }

    private Mono<String> buildScope(Account account) {
        if (CollectionUtils.isEmpty(account.getRoleIds())) {
            return Mono.just("");
        }

        return roleRepository.findAllById(account.getRoleIds())
                .map(role -> role.getName())
                .collectList()
                .map(roleNames -> String.join(" ", roleNames));
    }
}
