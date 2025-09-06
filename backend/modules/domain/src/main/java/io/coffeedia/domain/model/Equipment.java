package io.coffeedia.domain.model;

import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.EquipmentType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record Equipment(
    Long id,
    Long userId,
    EquipmentType type,
    String name,
    String brand,
    ActiveStatus status,
    String description,
    LocalDate buyDate,
    String buyUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public Equipment {
        // 필수 필드 검증
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        if (type == null) {
            throw new IllegalArgumentException("장비 타입은 필수입니다.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("장비 이름은 필수입니다.");
        }
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("장비 브랜드는 필수입니다.");
        }

        // 구매 일자 검증 (미래 날짜 불가)
        if (buyDate != null && buyDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("구매일자는 현재 날짜보다 이후일 수 없습니다.");
        }

        // URL 형식 간단 검증
        if (buyUrl != null && !buyUrl.isBlank() && !isValidUrl(buyUrl)) {
            throw new IllegalArgumentException("유효하지 않은 구매 URL 형식입니다.");
        }

        // 기본값 설정
        if (status == null) {
            status = ActiveStatus.ACTIVE;
        }
    }

    private static boolean isValidUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
