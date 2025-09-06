package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.PageSize;
import io.coffeedia.domain.vo.SortType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record RecipeSearchQuery(
    PageSize pageSize,
    List<SortType> sort
) {

    public static RecipeSearchQuery of(
        int page,
        int size,
        String sort
    ) {
        List<SortType> validSort = parseSort(sort);
        return RecipeSearchQuery.builder()
            .pageSize(new PageSize(page, size))
            .sort(validSort)
            .build();
    }

    /**
     * 정렬 조건 파싱("field1:asc,field2:desc")
     */
    private static List<SortType> parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return List.of(SortType.CREATED_AT_DESC); // 기본값
        }
        String[] sortParameters = sort.split(",");
        return Arrays.stream(sortParameters)
            .map(RecipeSearchQuery::parseSingleSort)
            .collect(Collectors.toList());
    }

    private static SortType parseSingleSort(String singleSort) {
        if (singleSort == null || singleSort.isBlank()) {
            return SortType.CREATED_AT_DESC;
        }
        String[] parts = singleSort.split(":");
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException(
                String.format("잘못된 정렬 형식입니다. ('%s')", singleSort)
            );

        }

        String field = parts[0].trim();
        String direction = parts[1].trim();
        return SortType.sortType(field, direction);
    }
}
