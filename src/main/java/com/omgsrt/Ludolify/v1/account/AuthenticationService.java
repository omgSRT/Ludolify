package com.omgsrt.Ludolify.v1.account;

import com.omgsrt.Ludolify.v1.account.dto.request.LoginRequest;
import com.omgsrt.Ludolify.v1.account.dto.response.LoginResponse;
import reactor.core.publisher.Mono;

public interface AuthenticationService {
    Mono<LoginResponse> login(LoginRequest request);
}
