package com.omgsrt.Ludolify.v1.role;

import com.omgsrt.Ludolify.shared.pagination.PaginationResponse;
import com.omgsrt.Ludolify.shared.pagination.PaginationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class RoleServiceImpl implements RoleService{
    RoleRepository roleRepository;
    PaginationUtil paginationUtil;

    @Override
    public Mono<PaginationResponse<Role>> getAllRoles(int pageIndex, int pageSize) {
        return roleRepository.findAll()
                .collectList()
                .map(roles -> paginationUtil.pagingList(pageIndex, pageSize, roles));
    }
}
