package com.omgsrt.Ludolify.shared.account;

import com.omgsrt.Ludolify.shared.account.dto.request.LoginRequest;
import com.omgsrt.Ludolify.shared.account.dto.response.LoginResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/authentication")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {
    AuthenticationService authenticationService;

    @Operation(summary = "Login", description = "Login Using Username And Password")
    @PostMapping("/login")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
            description = "Bad Request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorCode.class)))
    public Mono<ResponseEntity<ApiResponse<LoginResponse>>> login(@RequestBody LoginRequest request) {
        return authenticationService.login(request)
                .map(loginResponse -> {
                    SuccessCode successCode = SuccessCode.SUCCESSFULLY_LOGIN;
                    return ResponseEntity
                            .status(successCode.getStatusCode())
                            .body(ApiResponse.<LoginResponse>builder()
                                    .code(successCode.getCode())
                                    .message(successCode.getMessage())
                                    .entity(loginResponse)
                                    .build());
                });
    }
}
