package com.omgsrt.Ludolify.shared.role;

import com.omgsrt.Ludolify.shared.pagination.PaginationResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import reactor.core.publisher.Mono;

public interface RoleService {
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    Mono<PaginationResponse<Role>> getAllRoles(int pageIndex, int pageSize);
}
