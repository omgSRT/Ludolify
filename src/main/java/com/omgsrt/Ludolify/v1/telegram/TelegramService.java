package com.omgsrt.Ludolify.v1.telegram;

import reactor.core.publisher.Mono;

public interface TelegramService {
    Mono<String> sendMessage(String message);
}
