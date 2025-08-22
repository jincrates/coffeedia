package io.coffeedia.boostrap.api.dto;

public record BaseResponse<T>(
    boolean success,
    String message,
    T data
) {

    public static <T> BaseResponse<T> of(
        boolean success,
        String message,
        T data
    ) {
        return new BaseResponse<>(
            success,
            message,
            data
        );
    }
}
