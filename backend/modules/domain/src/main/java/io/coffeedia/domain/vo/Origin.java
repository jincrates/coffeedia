package io.coffeedia.domain.vo;

import lombok.Builder;

@Builder
public record Origin(
    String country,
    String region
) {
    public Origin {
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("원산지 국가는 필수입니다.");
        }
    }
}
