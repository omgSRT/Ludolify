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
                .map(jwt -> true)
                .defaultIfEmpty(false);
    }

    private Mono<SignedJWT> verifyToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                JWSObject jwsObject = JWSObject.parse(token);
                MACVerifier verifier = new MACVerifier(jwtSecret.getBytes());

                if (!jwsObject.verify(verifier)) {
                    return null;
                }

                SignedJWT signedJWT = SignedJWT.parse(token);
                Date expiredDate = signedJWT.getJWTClaimsSet().getExpirationTime();
                if (expiredDate.before(new Date())) {
                    return null;
                }

                return signedJWT;
            } catch (JOSEException | ParseException e) {
                throw new AppException(ErrorCode.UNDEFINED_EXCEPTION);
            }
        }).onErrorResume(e -> {
            log.error("Token verification failed: {}", e.getMessage());
            return Mono.empty();
        });
    }
}
