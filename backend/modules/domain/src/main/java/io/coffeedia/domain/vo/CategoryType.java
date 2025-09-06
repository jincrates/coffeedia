package io.coffeedia.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryType {
    HAND_DRIP("핸드드립"),
    ESPRESSO("에스프레소"),
    COLD_BREW("콜드브루"),
    MOCHA_POT("모카포트"),
    ;

    private final String description;
}
