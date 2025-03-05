package com.omgsrt.Ludolify.shared.security;

import com.omgsrt.Ludolify.shared.account.Account;
import com.omgsrt.Ludolify.shared.account.AccountRepository;
import com.omgsrt.Ludolify.shared.role.Role;
import com.omgsrt.Ludolify.shared.role.RoleRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class SpringSecurityServiceImpl implements SpringSecurityService {
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

    public boolean validateJwt(Account account, String token) {
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        var isNotExpired = claims.getExpiration().after(Date.from(Instant.now()));

        return isNotExpired && account.getUsername().equalsIgnoreCase(claims.getSubject());
    }

    public String getSubject(String token) {
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        return claims.getSubject();
    }

    public Mono<Account> getAccountByUsername(String username) {
        return accountRepository.findByUsernameContainingIgnoreCase(username);
    }

    public Flux<Role> findAllRolesByIds(Set<ObjectId> roleIds) {
        return roleRepository.findAllById(roleIds);
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();
        return claims.get("roles", List.class); // Extract roles from token
    }
}
