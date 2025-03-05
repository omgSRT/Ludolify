package com.omgsrt.Ludolify.shared.account;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, ObjectId> {
    Mono<Account> findByUsernameContainingIgnoreCase(String name);
}
