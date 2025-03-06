package com.omgsrt.Ludolify.shared.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomSecurityContextRepository implements ServerSecurityContextRepository {
    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        log.info("SecurityContextRepository: Loading SecurityContext for path: {}", exchange.getRequest().getPath());
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.defer(() -> {
                    SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
                    return Mono.just(emptyContext);
                }))
                .doOnNext(context -> log.info("SecurityContextRepository: Loaded SecurityContext: {}", context));
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        log.info("SecurityContextRepository: Saving SecurityContext for path: {}, context: {}",
                exchange.getRequest().getPath(), context);
        if (context == null) {
            return Mono.empty();
        }
        return Mono.just(context)
                .contextWrite(ctx -> ctx.put(SecurityContext.class, context))
                .then();
    }
}
