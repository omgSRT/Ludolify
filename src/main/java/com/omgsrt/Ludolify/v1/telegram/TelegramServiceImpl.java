package com.omgsrt.Ludolify.v1.telegram;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class TelegramServiceImpl implements TelegramService {
    WebClient webClient = WebClient.create("https://api.telegram.org");

    @Value("${telegram.bot.token}")
    @NonFinal
    String botToken;

    @Value("${telegram.chat.id}")
    @NonFinal
    String chatId;

    @Override
    public Mono<String> sendMessage(String message) {
        String url = "/bot" + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + message;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);
    }
}
