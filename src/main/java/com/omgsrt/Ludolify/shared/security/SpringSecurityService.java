package com.omgsrt.Ludolify.shared.security;

import com.omgsrt.Ludolify.shared.account.Account;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SpringSecurityService {
    boolean validateJwt(Account account, String token);

    String getSubject(String token);

    Mono<Account> getAccountByUsername(String username);

    List<String> getRolesFromToken(String token);
}
