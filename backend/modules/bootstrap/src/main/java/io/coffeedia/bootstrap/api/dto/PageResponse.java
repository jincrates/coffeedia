package io.coffeedia.bootstrap.api.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record PageResponse<T>(
    int page,
    boolean hasNext,
    List<T> content
) {

    public static <T> PageResponse<T> of(
        final int page,
        final int size,
        final List<T> contents
    ) {
        boolean hasNext = contents.size() > size;
        List<T> actualContent = hasNext
            ? contents.subList(0, size)  // 초과분 제거
            : contents;

        return PageResponse.<T>builder()
            .page(page)
            .hasNext(hasNext)
            .content(actualContent)
            .build();
    }
}
