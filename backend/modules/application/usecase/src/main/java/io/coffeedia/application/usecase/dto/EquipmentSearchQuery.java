package io.coffeedia.application.usecase.dto;

import io.coffeedia.domain.vo.PageSize;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record EquipmentSearchQuery(
    PageSize pageSize
) {

    public static EquipmentSearchQuery of(
        int page,
        int size
    ) {
        return EquipmentSearchQuery.builder()
            .pageSize(new PageSize(page, size))
            .build();
    }
}
