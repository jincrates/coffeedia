package io.coffeedia.domain.exception;

public class EquipmentNotFoundException extends RuntimeException {
    
    public EquipmentNotFoundException(Long equipmentId) {
        super(String.format("장비를 찾을 수 없습니다. (ID: %d)", equipmentId));
    }
}
