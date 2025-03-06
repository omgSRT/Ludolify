package com.omgsrt.Ludolify.shared.role;

import com.omgsrt.Ludolify.shared.pagination.PaginationResponse;
import reactor.core.publisher.Mono;

public interface RoleService {
    Mono<PaginationResponse<Role>> getAllRoles(int pageIndex, int pageSize);
}
