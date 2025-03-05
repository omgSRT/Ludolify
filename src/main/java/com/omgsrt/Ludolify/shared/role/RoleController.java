package com.omgsrt.Ludolify.shared.role;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.omgsrt.Ludolify.shared.dto.response.ApiResponse;
import com.omgsrt.Ludolify.shared.enums.SuccessCode;
import com.omgsrt.Ludolify.shared.exception.ErrorCode;
import com.omgsrt.Ludolify.shared.pagination.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/role")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @Operation(summary = "Get Roles")
    @GetMapping("/")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginationResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
            description = "Bad Request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorCode.class)))
    public Mono<ResponseEntity<ApiResponse<PaginationResponse<Role>>>> getAllRoles(@RequestParam(defaultValue = "1") int pageIndex,
                                                                                   @RequestParam(defaultValue = "10") int pageSize) {
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
