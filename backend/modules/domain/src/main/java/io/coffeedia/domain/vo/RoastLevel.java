package io.coffeedia.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoastLevel {
    LIGHT("약배전"),
    MEDIUM("중배전"),
    DARK("강배전"),
    UNKNOWN("알 수 없음"),
    ;

    private final String description;
}
