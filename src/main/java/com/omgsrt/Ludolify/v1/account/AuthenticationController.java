package com.omgsrt.Ludolify.v1.account;

import com.omgsrt.Ludolify.shared.dto.response.ApiResponse;
import com.omgsrt.Ludolify.shared.enums.SuccessCode;
import com.omgsrt.Ludolify.v1.account.dto.request.LoginRequest;
import com.omgsrt.Ludolify.v1.account.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
            schema = @Schema(implementation = ApiResponse.class)))
    public Mono<ResponseEntity<ApiResponse<LoginResponse>>> login(@RequestBody LoginRequest request){
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
