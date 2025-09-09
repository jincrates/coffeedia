package io.coffeedia.domain.exception;

/**
 * 접근 권한이 없는 경우 발생하는 예외
 */
public class ForbiddenException extends RuntimeException {
    
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
