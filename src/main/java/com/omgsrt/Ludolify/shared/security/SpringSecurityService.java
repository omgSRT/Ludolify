package com.omgsrt.Ludolify.shared.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.omgsrt.Ludolify.shared.exception.AppException;
import com.omgsrt.Ludolify.shared.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.Date;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
public class SpringSecurityService {
    @Value("${spring.jwt.secret}")
    String jwtSecret;

    public Mono<Boolean> introspectJWT(String token) {
        return verifyToken(token)
                .map(jwt -> {
                    try {
                        log.info("Token is valid: {}", jwt.getJWTClaimsSet().getSubject());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                })
                .onErrorResume(e -> {
                    log.error("JWT introspection failed: {}", e.getMessage());
                    return Mono.just(false);
                });
    }

    private Mono<SignedJWT> verifyToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                SignedJWT signedJWT = SignedJWT.parse(token);
                MACVerifier verifier = new MACVerifier(jwtSecret.getBytes());

                if (!signedJWT.verify(verifier)) {
                    log.warn("JWT verification failed: Signature does not match");
                    throw new AppException(ErrorCode.UNDEFINED_EXCEPTION);
                }

                Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
                if (expirationDate.before(new Date())) {
                    log.warn("JWT expired at {}", expirationDate);
                    throw new AppException(ErrorCode.UNDEFINED_EXCEPTION);
                }

                return signedJWT;
            } catch (JOSEException | ParseException e) {
                log.error("JWT parsing/verifying failed", e);
                throw new AppException(ErrorCode.UNDEFINED_EXCEPTION);
            }
        }).onErrorResume(e -> {
            log.error("Token verification failed: {}", e.getMessage());
            return Mono.empty();
        });
    }
}
