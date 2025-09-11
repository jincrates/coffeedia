package io.coffeedia.bootstrap.api.controller;

import io.coffeedia.application.usecase.CreateUserUseCase;
import io.coffeedia.application.usecase.GetCurrentUserUseCase;
import io.coffeedia.application.usecase.dto.CreateUserCommand;
import io.coffeedia.application.usecase.dto.UserResponse;
import io.coffeedia.bootstrap.api.controller.docs.AuthControllerDocs;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginRequest;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginResponse;
import io.coffeedia.bootstrap.api.controller.dto.auth.RefreshTokenRequest;
import io.coffeedia.bootstrap.api.controller.dto.auth.SignUpRequest;
import io.coffeedia.bootstrap.api.controller.dto.auth.SignUpResponse;
import io.coffeedia.bootstrap.api.controller.dto.auth.UserInfoResponse;
import io.coffeedia.bootstrap.api.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final CreateUserUseCase createUserUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    @Override
    public ResponseEntity<BaseResponse<SignUpResponse>> signUp(
        @Valid @RequestBody SignUpRequest request
    ) {
        log.info("회원가입 요청: {}", request.getUsername());

        // 비밀번호 확인 검증
        if (!request.isPasswordMatched()) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다");
        }

        // 비밀번호 암호화 (인프라스트럭처 관심사)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        CreateUserCommand command = CreateUserCommand.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(encodedPassword) // 암호화된 비밀번호 전달
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .build();

        UserResponse userResponse = createUserUseCase.execute(command);
        SignUpResponse response = SignUpResponse.from(userResponse);

        log.info("회원가입 성공: {} (ID: {})", request.getUsername(), userResponse.id());
        return created(response);
    }

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
