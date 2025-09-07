package io.coffeedia.domain.exception;

/**
 * 레시피를 찾을 수 없는 경우 발생하는 예외
 */
public class RecipeNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "레시피를 찾을 수 없습니다.";

    public RecipeNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public RecipeNotFoundException(String message) {
        super(message);
    }

    public RecipeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecipeNotFoundException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
