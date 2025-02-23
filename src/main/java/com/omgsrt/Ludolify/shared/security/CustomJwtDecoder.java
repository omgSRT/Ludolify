package com.omgsrt.Ludolify.shared.security;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomJwtDecoder implements ReactiveJwtDecoder {
    SpringSecurityService springSecurityService;
    @NonFinal
    NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder;
    @Value("${spring.jwt.secret}")
    @NonFinal
    private String secretKey;

    @PostConstruct
    public void initDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
        this.nimbusReactiveJwtDecoder = NimbusReactiveJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Mono<Jwt> decode(String token) {
        return springSecurityService.introspectJWT(token)
                .flatMap(isValid -> isValid
                        ? nimbusReactiveJwtDecoder.decode(token)
                        : Mono.error(new JwtException("Token invalid"))
                )
                .onErrorResume(e -> Mono.error(new JwtException("JWT introspection failed", e)));
    }
}
