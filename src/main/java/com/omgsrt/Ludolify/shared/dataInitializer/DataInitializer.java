package com.omgsrt.Ludolify.shared.dataInitializer;

import reactor.core.publisher.Mono;

public interface DataInitializer {
    Mono<Void> initialize();
}
