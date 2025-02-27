package com.omgsrt.Ludolify.shared.pagination;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationResponse<T> {
    long totalElements;
    int totalPages;
    int currentPage;
    int pageSize;
    List<T> content;
}
