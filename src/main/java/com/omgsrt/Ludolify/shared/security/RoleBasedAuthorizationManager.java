package com.omgsrt.Ludolify.shared.security;

import com.omgsrt.Ludolify.shared.enums.RolePath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class RoleBasedAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    private final Map<String, Set<String>> pathRoleMapping = new HashMap<>();
    {
        //v1 path role
        pathRoleMapping.put("/role/**", Set.of(RolePath.ADMINISTRATOR.name()));
        pathRoleMapping.put("/user/**", Set.of(RolePath.ADMINISTRATOR.name(), RolePath.MEMBER.name()));
    }

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext context) {
        ServerWebExchange exchange = context.getExchange();
        String path = exchange.getRequest().getPath().value();

        Set<String> requiredRoles = pathRoleMapping.entrySet().stream()
                .filter(entry -> pathMatcher.match(entry.getKey(), path))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(Set.of());

        if (requiredRoles.isEmpty()) {
            return Mono.just(new AuthorizationDecision(true));
        }

        return authentication
                .flatMap(auth -> {
                    if (auth == null) {
                        log.warn("Access denied to {}: No authentication provided", path);
                        return Mono.just(new AuthorizationDecision(false));
                    }
                    boolean hasRole = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .anyMatch(requiredRoles::contains);
                    if (!hasRole) {
                        log.warn("Access denied to {}: Required roles {} not found in {}",
                                path, requiredRoles, auth.getAuthorities());
                        return Mono.just(new AuthorizationDecision(false));
                    }
                    return Mono.just(new AuthorizationDecision(true));
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
