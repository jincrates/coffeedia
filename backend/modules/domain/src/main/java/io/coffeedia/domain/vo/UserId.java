package io.coffeedia.domain.vo;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 ID Value Object
 */
@Getter
@Builder
public class UserId {
    
    private final String value;

    public UserId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 비어있을 수 없습니다");
        }
        this.value = value.trim();
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserId userId = (UserId) obj;
        return value.equals(userId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
