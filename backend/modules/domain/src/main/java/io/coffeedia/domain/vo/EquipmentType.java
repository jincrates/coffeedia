package io.coffeedia.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EquipmentType {
    MACHINE("머신"),
    GRINDER("그라인더"),
    SCALE("저울"),
    ;

    private final String description;
}
