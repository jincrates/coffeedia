package io.coffeedia.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlendType {
    SINGLE_ORIGIN("싱글오리진"),
    BLEND("블렌드"),
    UNKNOWN("알 수 없음"),
    ;

    private final String description;
}
