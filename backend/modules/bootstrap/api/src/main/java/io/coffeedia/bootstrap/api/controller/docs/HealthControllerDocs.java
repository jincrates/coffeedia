package io.coffeedia.bootstrap.api.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;

@Tag(name = "시스템 상태", description = "애플리케이션 상태 확인 API")
public interface HealthControllerDocs {

    @Operation(
        summary = "헬스 체크",
        description = "애플리케이션의 상태를 확인합니다. 서버가 정상적으로 실행 중인지 확인할 수 있으며, " +
            "로컬 포트와 서버 포트 정보를 포함한 상태 메시지를 반환합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "서버가 정상적으로 실행 중",
            content = @Content(
                mediaType = MediaType.TEXT_PLAIN_VALUE,
                examples = @ExampleObject(
                    name = "정상 응답",
                    summary = "헬스 체크 성공 응답",
                    value = "It's Working in Coffeedia Backend on LOCAL PORT 8080 (SERVER PORT 8080)"
                )
            )
        ),
        @ApiResponse(
            responseCode = "503",
            description = "서버가 일시적으로 사용 불가능한 상태",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)
        )
    })
    String health();
}
