package com.omgsrt.Ludolify.shared.role;

import com.omgsrt.Ludolify.shared.pagination.PaginationResponse;
import com.omgsrt.Ludolify.shared.pagination.PaginationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    PaginationUtil paginationUtil;

    @Override
    public Mono<PaginationResponse<Role>> getAllRoles(int pageIndex, int pageSize) {
        log.info("RoleServiceImpl: getAllRoles called with pageIndex: {}, pageSize: {}", pageIndex, pageSize);
        return roleRepository.findAll()
                .collectList()
                .map(roles -> paginationUtil.pagingList(pageIndex, pageSize, roles));
    }
}
