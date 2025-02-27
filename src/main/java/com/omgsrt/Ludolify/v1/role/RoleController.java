package com.omgsrt.Ludolify.v1.role;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.omgsrt.Ludolify.shared.dto.response.ApiResponse;
import com.omgsrt.Ludolify.shared.enums.SuccessCode;
import com.omgsrt.Ludolify.shared.pagination.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/role")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @Operation(summary = "Get Roles")
    @GetMapping("/")
    public Mono<ResponseEntity<ApiResponse<PaginationResponse<Role>>>> getAllRoles(@RequestParam(defaultValue = "1") int pageIndex,
                                                                                   @RequestParam(defaultValue = "10") int pageSize){
        return roleService.getAllRoles(pageIndex, pageSize)
                .map(paginationResponse -> {
                    boolean isEmpty = CollectionUtils.isEmpty(paginationResponse.getContent());
                    SuccessCode successCode = isEmpty
                            ? SuccessCode.SUCCESSFULLY_GET_WITH_NO_CONTENT
                            : SuccessCode.SUCCESSFULLY_GET_WITH_CONTENT;

                    return ResponseEntity
                            .status(successCode.getStatusCode())
                            .body(ApiResponse.<PaginationResponse<Role>>builder()
                                    .code(successCode.getCode())
                                    .message(successCode.getMessage())
                                    .entity(paginationResponse)
                                    .build());
                });
    }
}
