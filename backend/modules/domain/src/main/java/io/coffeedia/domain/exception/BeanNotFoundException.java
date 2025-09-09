package io.coffeedia.domain.exception;

public class BeanNotFoundException extends RuntimeException {
    
    public BeanNotFoundException(Long beanId) {
        super(String.format("원두를 찾을 수 없습니다. (ID: %d)", beanId));
    }
}
