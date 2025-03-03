package com.omgsrt.Ludolify.v1.role;

import com.omgsrt.Ludolify.shared.pagination.PaginationResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import reactor.core.publisher.Mono;

public interface RoleService {
    Mono<PaginationResponse<Role>> getAllRoles(int pageIndex, int pageSize);
}
