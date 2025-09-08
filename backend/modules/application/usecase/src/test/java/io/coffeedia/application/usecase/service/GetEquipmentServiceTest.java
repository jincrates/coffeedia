package io.coffeedia.application.usecase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.coffeedia.application.usecase.dto.EquipmentResponse;
import io.coffeedia.domain.exception.EquipmentNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("GetEquipmentService 테스트")
class GetEquipmentServiceTest {

    @Nested
    @DisplayName("장비 조회 테스트")
    class GetEquipmentTest {

        @Test
        @DisplayName("존재하는 장비 ID로 조회하면 성공한다")
        void getEquipment_Success() {
            // Given
            Long equipmentId = 1L;
            
            // When & Then
            // TODO: Mock을 이용한 실제 테스트 구현 필요
            // 여기서는 테스트 구조만 작성
        }

        @Test
        @DisplayName("존재하지 않는 장비 ID로 조회하면 예외가 발생한다")
        void getEquipment_NotFound() {
            // Given
            Long nonExistentId = 999L;
            
            // When & Then
            // TODO: Mock을 이용한 실제 테스트 구현 필요
            // assertThatThrownBy(() -> getEquipmentService.invoke(nonExistentId))
            //     .isInstanceOf(EquipmentNotFoundException.class);
        }
    }
}
