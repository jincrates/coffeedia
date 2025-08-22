package io.coffeedia.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProcessType {
    WASHED("워시드"),
    NATURAL("내추럴"),
    UNKNOWN("알 수 없음"),
    ;

    private final String description;
}
