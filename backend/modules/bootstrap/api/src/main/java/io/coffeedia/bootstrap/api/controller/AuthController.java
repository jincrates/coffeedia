package io.coffeedia.bootstrap.api.controller;

import io.coffeedia.application.usecase.GetCurrentUserUseCase;
import io.coffeedia.application.usecase.dto.UserResponse;
import io.coffeedia.bootstrap.api.controller.docs.AuthControllerDocs;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginRequest;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginResponse;
import io.coffeedia.bootstrap.api.controller.dto.auth.RefreshTokenRequest;
import io.coffeedia.bootstrap.api.controller.dto.auth.UserInfoResponse;
import io.coffeedia.bootstrap.api.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 REST API
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController implements AuthControllerDocs {

    private final AuthenticationService authenticationService;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
        @Valid @RequestBody LoginRequest request
    ) {
        log.info("로그인 요청: {}", request.getUsername());

        LoginResponse response = authenticationService.login(request);

        log.info("로그인 성공: {}", request.getUsername());
        return ok(response);
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<LoginResponse>> refresh(
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        log.info("토큰 갱신 요청");

        LoginResponse response = authenticationService.refreshToken(request.getRefreshToken());

        log.info("토큰 갱신 성공");
        return ok(response);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("로그아웃 요청: {}", userDetails.getUsername());

        authenticationService.logout(userDetails.getUsername());

        log.info("로그아웃 성공: {}", userDetails.getUsername());
        return ok(null);
    }

    /**
     * 현재 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserInfoResponse>> getCurrentUser(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.debug("현재 사용자 정보 조회: {}", userDetails.getUsername());

        UserResponse user = getCurrentUserUseCase.execute(userDetails.getUsername());
        UserInfoResponse response = UserInfoResponse.from(user);

        return ok(response);
    }

    /**
     * 토큰 유효성 검증
     */
    @PostMapping("/validate")
    public ResponseEntity<BaseResponse<Boolean>> validateToken(
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        log.debug("토큰 유효성 검증 요청");

        boolean isValid = authenticationService.validateToken(authorizationHeader);

        return ok(isValid);
    }
}
