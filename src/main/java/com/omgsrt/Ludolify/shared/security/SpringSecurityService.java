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

//    public String generateToken(int expirationDay) {
//        Date now = new Date();
//        Instant nowInstant = now.toInstant();
//        Instant expirationInstant = nowInstant.plus(expirationDay, ChronoUnit.DAYS);
//        Date expirationTime = Date.from(expirationInstant);
//
//        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
//
//        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
//                .issuer("dev-Yummigo")
//                .issueTime(now)
//                .expirationTime(expirationTime)
//                .jwtID(UUID.randomUUID().toString())
//                //.claim("scope", buildScope(account))
//                .build();
//
//        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
//
//        JWSObject jwsObject = new JWSObject(header, payload);
//        try {
//            jwsObject.sign(new MACSigner(jwtSecret.getBytes()));
//            return jwsObject.serialize();
//        } catch (JOSEException e) {
//            throw new RuntimeException(e);
//        }
//    }

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
//    private String buildScope(Account account) {
//        StringJoiner stringJoiner = new StringJoiner(" ");
//        if (account.getRole() != null && account.getRole().getName() != null) {
//            stringJoiner.add(account.getRole().getName());
//        }
//        return stringJoiner.toString();
//    }
}
