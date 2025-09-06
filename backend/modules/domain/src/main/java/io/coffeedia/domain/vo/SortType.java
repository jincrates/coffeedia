package io.coffeedia.domain.vo;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
    CREATED_AT_ASC("createdAt", "asc"),
    CREATED_AT_DESC("createdAt", "desc"),
    ;

    private final String field;
    private final String direction;

    public static SortType sortType(final String field, final String direction) {
        return Arrays.stream(values())
            .filter(sortType -> isMatching(sortType, field, direction))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format(
                    "지원하지 않는 정렬 타입입니다. (field: '%s' and direction: '%s')",
                    field, direction
                )
            ));

    }

    private static boolean isMatching(
        final SortType sortType,
        final String field,
        final String direction
    ) {
        String normalizedDirection = direction.toLowerCase().trim();
        return sortType.field.equals(field) && sortType.direction.equals(normalizedDirection);
    }
}
