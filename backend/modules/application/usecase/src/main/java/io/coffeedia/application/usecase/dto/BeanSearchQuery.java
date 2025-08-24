package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.BeanSortType;
import io.coffeedia.domain.vo.PageSize;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record BeanSearchQuery(
    PageSize pageSize,
    List<BeanSortType> sort
) {

    public static BeanSearchQuery of(
        int page,
        int size,
        String sort
    ) {
        List<BeanSortType> validSort = parseSort(sort);
        return BeanSearchQuery.builder()
            .pageSize(new PageSize(page, size))
            .sort(validSort)
            .build();
    }

    /**
     * 정렬 조건 파싱("field1:asc,field2:desc")
     */
    private static List<BeanSortType> parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return List.of(BeanSortType.CREATED_AT_DESC); // 기본값
        }
        String[] sortParameters = sort.split(",");
        return Arrays.stream(sortParameters)
            .map(BeanSearchQuery::parseSingleSort)
            .collect(Collectors.toList());
    }

    private static BeanSortType parseSingleSort(String singleSort) {
        if (singleSort == null || singleSort.isBlank()) {
            return BeanSortType.CREATED_AT_DESC;
        }
        String[] parts = singleSort.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                String.format("잘못된 정렬 형식입니다. ('%s')", singleSort)
            );

        }

        String field = parts[0].trim();
        String direction = parts[1].trim();
        return BeanSortType.sortType(field, direction);
    }
}
