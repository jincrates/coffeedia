package io.coffeedia.domain.vo;

public record PageSize(
    int page,
    int size
) {

    public PageSize {
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 0;
        }
    }
}
