package com.omgsrt.Ludolify.shared.role;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RoleRepository extends ReactiveMongoRepository<Role, ObjectId> {
    Mono<Role> findByNameContainingIgnoreCase(String name);
}
