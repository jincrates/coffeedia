package io.coffeedia.bootstrap.api.controller.docs;

import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginRequest;
import io.coffeedia.bootstrap.api.controller.dto.auth.LoginResponse;
import io.coffeedia.bootstrap.api.controller.dto.auth.RefreshTokenRequest;
import io.coffeedia.bootstrap.api.controller.dto.auth.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Auth Controller API 문서화 인터페이스
 */
@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthControllerDocs {

    @Operation(
            summary = "로그인",
            description = "사용자명과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<BaseResponse<LoginResponse>> login(LoginRequest request);

    @Operation(
            summary = "토큰 갱신",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
    })
    ResponseEntity<BaseResponse<LoginResponse>> refresh(RefreshTokenRequest request);

    @Operation(
            summary = "로그아웃",
            description = "현재 사용자를 로그아웃 처리합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<BaseResponse<Void>> logout(
            @Parameter(hidden = true) UserDetails userDetails
    );

    @Operation(
            summary = "현재 사용자 정보 조회",
            description = "JWT 토큰을 통해 현재 로그인한 사용자의 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    ResponseEntity<BaseResponse<UserInfoResponse>> getCurrentUser(
            @Parameter(hidden = true) UserDetails userDetails
    );

    @Operation(
            summary = "토큰 유효성 검증",
            description = "JWT 토큰의 유효성을 검증합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 검증 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<BaseResponse<Boolean>> validateToken(
            @Parameter(description = "Bearer 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            String authorizationHeader
    );
}
