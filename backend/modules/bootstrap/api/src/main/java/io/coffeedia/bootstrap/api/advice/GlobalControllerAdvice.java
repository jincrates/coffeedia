package io.coffeedia.bootstrap.api.advice;

import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.domain.exception.AccessDeniedException;
import io.coffeedia.domain.exception.BeanNotFoundException;
import io.coffeedia.domain.exception.EquipmentNotFoundException;
import io.coffeedia.domain.exception.RecipeNotFoundException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "서버에서 에러가 발생했습니다.";
    private static final String VALIDATION_ERROR_FORMAT = "%s (입력값: %s)";

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
        Exception exception,
        Object body,
        HttpHeaders headers,
        HttpStatusCode statusCode,
        WebRequest request
    ) {
        log.warn(
            "Internal exception occurred - status: {}, message: {}, path: {}",
            statusCode,
            exception.getMessage(),
            getRequestPath(request),
            exception
        );
        return error(
            statusCode,
            exception.getMessage()
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException exception,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        String message = formatBindingResultMessage(exception.getBindingResult());

        log.warn(
            "Validation failed - path: {}, errors: {}",
            getRequestPath(request),
            message
        );

        return error(
            HttpStatus.BAD_REQUEST,
            message
        );
    }

    /**
     * IllegalArgumentException 에러
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<?> handleIllegalArgumentException(
        IllegalArgumentException exception,
        WebRequest request
    ) {
        log.warn(
            "Invalid argument - path: {}, message: {}",
            getRequestPath(request),
            exception.getMessage()
        );

        return error(
            HttpStatus.BAD_REQUEST,
            exception.getMessage()
        );
    }

    /**
     * RecipeNotFoundException 에러 - 레시피를 찾을 수 없는 경우
     */
    @ExceptionHandler(RecipeNotFoundException.class)
    protected ResponseEntity<?> handleRecipeNotFoundException(
        RecipeNotFoundException exception,
        WebRequest request
    ) {
        log.warn(
            "Recipe not found - path: {}, message: {}",
            getRequestPath(request),
            exception.getMessage()
        );

        return error(
            HttpStatus.BAD_REQUEST,
            exception.getMessage()
        );
    }

    /**
     * BeanNotFoundException 에러 - 원두를 찾을 수 없는 경우
     */
    @ExceptionHandler(BeanNotFoundException.class)
    protected ResponseEntity<?> handleBeanNotFoundException(
        BeanNotFoundException exception,
        WebRequest request
    ) {
        log.warn(
            "Bean not found - path: {}, message: {}",
            getRequestPath(request),
            exception.getMessage()
        );

        return error(
            HttpStatus.BAD_REQUEST,
            exception.getMessage()
        );
    }

    /**
     * EquipmentNotFoundException 에러 - 장비를 찾을 수 없는 경우
     */
    @ExceptionHandler(EquipmentNotFoundException.class)
    protected ResponseEntity<?> handleEquipmentNotFoundException(
        EquipmentNotFoundException exception,
        WebRequest request
    ) {
        log.warn(
            "Equipment not found - path: {}, message: {}",
            getRequestPath(request),
            exception.getMessage()
        );

        return error(
            HttpStatus.BAD_REQUEST,
            exception.getMessage()
        );
    }

    /**
     * 예상하지 못한 에러
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(Exception exception) {
        log.error(
            "Unexpected error occurred - type: {}, message: {}",
            exception.getClass().getSimpleName(),
            exception.getMessage(),
            exception
        );
        return error(
            HttpStatus.INTERNAL_SERVER_ERROR,
            INTERNAL_SERVER_ERROR_MESSAGE
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<?> handleAccessDeniedException(
        AccessDeniedException exception,
        WebRequest request
    ) {
        log.warn(
            "Access denied - path: {}, message: {}",
            getRequestPath(request),
            exception.getMessage()
        );
        return error(
            HttpStatus.FORBIDDEN,  // 403 Forbidden
            exception.getMessage()
        );
    }

//    /**
//     * 비즈니스 에러 처리 (예상 가능한 에러)
//     */
//    @ExceptionHandler(BusinessException.class)
//    protected ResponseEntity<?> handleBusinessException(
//        BusinessException exception,
//        WebRequest request
//    ) {
//        log.info(
//            "Business logic violation - path: {}, message: {}, arguments: {}",
//            getRequestPath(request),
//            exception.getMessage(),
//            exception.arguments()
//        );
//        return error(
//            HttpStatus.BAD_REQUEST,
//            exception.getMessage()
//        );
//    }
//
//    /**
//     * 서버 에러 처리 (시스템 에러)
//     */
//    @ExceptionHandler(ServerException.class)
//    protected ResponseEntity<?> handleServerException(
//        ServerException exception,
//        WebRequest request
//    ) {
//        log.error(
//            "Server error occurred - path: {}, message: {}, arguments: {}",
//            getRequestPath(request),
//            exception.getMessage(),
//            exception.arguments(),
//            exception
//        );
//        return error(
//            HttpStatus.BAD_REQUEST,
//            exception.getMessage()
//        );
//    }

    /**
     * 에러 응답 생성
     */
    private ResponseEntity<Object> error(
        HttpStatusCode httpStatus,
        String message
    ) {
        return ResponseEntity.status(httpStatus)
            .body(BaseResponse.of(
                httpStatus.is2xxSuccessful(),
                message,
                null
            ));
    }

    /**
     * 검증 에러 포맷팅
     */
    private String formatBindingResultMessage(BindingResult bindingResult) {
        if (bindingResult.getFieldErrors().isEmpty()) {
            return "입력값이 유효하지 않습니다.";
        }

        return bindingResult.getFieldErrors().stream()
            .map(error -> String.format(
                VALIDATION_ERROR_FORMAT,
                error.getDefaultMessage(),
                error.getRejectedValue()
            ))
            .collect(Collectors.joining("\n"));
    }

    /**
     * 요청 경로 추출
     */
    private String getRequestPath(WebRequest request) {
        if (request instanceof org.springframework.web.context.request.ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return request.getDescription(false).replace("uri=", "");
    }
}
