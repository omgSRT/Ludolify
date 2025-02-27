package com.omgsrt.Ludolify.shared.pagination;

import com.omgsrt.Ludolify.shared.exception.AppException;
import com.omgsrt.Ludolify.shared.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class PaginationUtil {
    public <T> PaginationResponse<T> pagingList(int pageIndex, int pageSize, List<T> list) {
        if (list == null || list.isEmpty()) {
            list = new ArrayList<>();
        }
        if (pageIndex <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGE_NUMBER);
        }
        if (pageSize <= 0) {
            throw new AppException(ErrorCode.INVALID_PER_PAGE_NUMBER);
        }

        int start = (pageIndex - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());

        if (start >= list.size()) {
            return new PaginationResponse<>(
                    list.size(),
                    (int) Math.ceil((double) list.size() / pageSize),
                    pageIndex,
                    pageSize,
                    Collections.emptyList());
        }

        List<T> pagedContent = list.subList(start, end);
        int totalPages = (int) Math.ceil((double) list.size() / pageSize);

        return new PaginationResponse<>(
                list.size(),
                totalPages,
                pageIndex,
                pageSize,
                pagedContent);
    }
}
