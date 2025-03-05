package com.omgsrt.Ludolify.shared.account;

import com.omgsrt.Ludolify.shared.account.dto.request.LoginRequest;
import com.omgsrt.Ludolify.shared.account.dto.response.LoginResponse;
import reactor.core.publisher.Mono;

public interface AuthenticationService {
    Mono<LoginResponse> login(LoginRequest request);
}
