package io.coffeedia.domain.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(final String message) {
        super(message);
    }
}
