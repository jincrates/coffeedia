package io.coffeedia.bootstrap.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 인증되지 않은 사용자의 요청을 처리하는 엔트리 포인트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {

        log.warn("인증되지 않은 요청: {} {}", request.getMethod(), request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        BaseResponse<Object> errorResponse = BaseResponse.of(
            false,
            "접근 권한이 없습니다",
            null
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
