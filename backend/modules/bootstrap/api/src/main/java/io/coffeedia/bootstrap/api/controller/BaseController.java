package io.coffeedia.bootstrap.api.controller;

import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

abstract class BaseController {

    protected <T> ResponseEntity<BaseResponse<T>> ok(T data) {
        return response(
            HttpStatus.OK,
            null,
            data
        );
    }

    protected <T> ResponseEntity<BaseResponse<T>> created(T data) {
        return response(
            HttpStatus.CREATED,
            null,
            data
        );
    }

    protected <T> ResponseEntity<BaseResponse<T>> response(
        HttpStatusCode httpStatus,
        String message,
        T data
    ) {
        return ResponseEntity.status(httpStatus)
            .body(BaseResponse.of(
                httpStatus.is2xxSuccessful(),
                message,
                data
            ));
    }
}
