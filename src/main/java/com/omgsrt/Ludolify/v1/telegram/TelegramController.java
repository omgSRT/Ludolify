package com.omgsrt.Ludolify.v1.telegram;

import com.omgsrt.Ludolify.shared.dto.response.ApiResponse;
import com.omgsrt.Ludolify.shared.enums.SuccessCode;
import com.omgsrt.Ludolify.shared.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/telegram")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TelegramController {
    TelegramService telegramService;

    @Operation(summary = "Send Message To Recipients")
    @GetMapping("/send_message")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
            description = "Bad Request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorCode.class)))
    public Mono<ResponseEntity<ApiResponse<String>>> sendMessage(@RequestParam String message) {
        return telegramService.sendMessage(message)
                .map(content -> {
                    SuccessCode successCode = SuccessCode.SUCCESSFULLY_SEND_MESSAGE;

                    return ResponseEntity
                            .status(successCode.getStatusCode())
                            .body(ApiResponse.<String>builder()
                                    .code(successCode.getCode())
                                    .message(successCode.getMessage())
                                    .build());
                });
    }
}
