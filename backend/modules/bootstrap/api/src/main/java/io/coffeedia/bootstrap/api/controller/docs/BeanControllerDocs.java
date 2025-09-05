package io.coffeedia.bootstrap.api.controller.docs;

import io.coffeedia.application.usecase.dto.BeanResponse;
import io.coffeedia.application.usecase.dto.CreateBeanCommand;
import io.coffeedia.application.usecase.dto.DeleteBeanResponse;
import io.coffeedia.application.usecase.dto.UpdateBeanCommand;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "원두 관리", description = "원두 등록, 수정, 조회, 목록 조회 API")
public interface BeanControllerDocs {

    @Operation(
        summary = "원두 등록",
        description = "새로운 원두 정보를 등록합니다. 원두 이름, 원산지, 로스터, 로스팅 일자, 보유 그램, 플레이버 정보는 필수이며, " +
            "로스팅 레벨, 가공 방식, 블렌드 타입, 디카페인 여부, 메모, 상태, 접근 권한 등을 추가로 설정할 수 있습니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                examples = {
                    @ExampleObject(
                        name = "필수 정보만 포함",
                        summary = "필수 정보만으로 원두 등록",
                        value = """
                            {
                                "name": "에티오피아 예가체프",
                                "origin": {
                                    "country": "에티오피아",
                                    "region": "예가체프"
                                },
                                "roaster": "커피로스터",
                                "roastDate": "2024-08-21",
                                "grams": 250,
                                "flavorIds": [1, 2]
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "전체 정보 포함",
                        summary = "모든 옵션 정보를 포함한 원두 등록",
                        value = """
                            {
                                "name": "콜롬비아 수프리모",
                                "origin": {
                                    "country": "콜롬비아",
                                    "region": "나리뇨"
                                },
                                "roaster": "스페셜티 로스터",
                                "roastDate": "2024-08-23",
                                "grams": 200,
                                "flavorIds": [1, 2],
                                "roastLevel": "MEDIUM",
                                "processType": "WASHED",
                                "blendType": "SINGLE_ORIGIN",
                                "isDecaf": false,
                                "memo": "산미가 좋은 원두",
                                "status": "ACTIVE",
                                "accessType": "PUBLIC"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "디카페인 원두",
                        summary = "디카페인 원두 등록 예시",
                        value = """
                            {
                                "name": "디카페인 콜롬비아",
                                "origin": {
                                    "country": "콜롬비아",
                                    "region": "우일라"
                                },
                                "roaster": "디카페인 전문 로스터",
                                "roastDate": "2024-08-22",
                                "grams": 200,
                                "flavorIds": [1, 2],
                                "isDecaf": true,
                                "processType": "WASHED"
                            }
                            """
                    )
                }
            )
        )
    )
    ResponseEntity<BaseResponse<BeanResponse>> createBean(
        @Parameter(description = "원두 등록 요청 데이터", required = true)
        @Valid @RequestBody CreateBeanCommand command
    );

    @Operation(
        summary = "원두 목록 조회",
        description = "등록된 원두 목록을 페이징과 정렬을 통해 조회합니다. " +
            "기본적으로 페이지당 10개씩 조회되며, 생성일시 기준으로 내림차순 정렬됩니다. " +
            "정렬은 'field:direction' 형식으로 지정하며, 여러 필드 정렬 시 콤마로 구분합니다.",
        parameters = {
            @Parameter(
                name = "page",
                description = "페이지 번호 (0부터 시작)",
                example = "0",
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "size",
                description = "페이지당 조회할 원두 개수",
                example = "10",
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "sort",
                description = "정렬 조건 (예: 'createdAt:desc', 'roastDate:asc,name:asc')",
                example = "createdAt:desc",
                in = ParameterIn.QUERY
            )
        }
    )
    ResponseEntity<BaseResponse<PageResponse<BeanResponse>>> getAllBeans(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort
    );

    @Operation(
        summary = "원두 상세 조회",
        description = "특정 원두의 상세 정보를 조회합니다. 원두 ID를 통해 해당 원두의 모든 정보를 확인할 수 있습니다."
    )
    ResponseEntity<BaseResponse<BeanResponse>> getBean(
        @Parameter(description = "조회할 원두 ID", required = true, in = ParameterIn.PATH, example = "1")
        @PathVariable Long beanId
    );

    @Operation(
        summary = "원두 정보 수정",
        description = "기존 원두의 정보를 수정합니다. 원두 이름, 보유 그램, 로스팅 레벨, 메모, 상태 등을 개별적으로 또는 " +
            "여러 필드를 한번에 수정할 수 있습니다. 수정하지 않는 필드는 기존 값이 유지됩니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                examples = {
                    @ExampleObject(
                        name = "이름 수정",
                        summary = "원두 이름만 수정",
                        value = """
                            {
                                "name": "수정된 원두 이름"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "로스팅 레벨 수정",
                        summary = "로스팅 레벨만 수정",
                        value = """
                            {
                                "roastLevel": "DARK"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "여러 필드 수정",
                        summary = "여러 필드를 한번에 수정",
                        value = """
                            {
                                "name": "종합 수정 테스트 원두",
                                "grams": 180,
                                "roastLevel": "DARK",
                                "memo": "여러 필드 수정 테스트",
                                "status": "INACTIVE"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "접근 권한 수정",
                        summary = "원두 접근 권한 변경",
                        value = """
                            {
                                "accessType": "PRIVATE"
                            }
                            """
                    )
                }
            )
        )
    )
    ResponseEntity<BaseResponse<BeanResponse>> updateBean(
        @Parameter(description = "수정할 원두 ID", required = true, in = ParameterIn.PATH, example = "1")
        @PathVariable Long beanId,
        @Parameter(description = "원두 수정 요청 데이터", required = true)
        @Valid @RequestBody UpdateBeanCommand command
    );

    @Operation(
        summary = "원두 삭제",
        description = "특정 원두를 삭제합니다. 삭제 요청 시 해당 원두가 현재 사용자의 소유인지 확인하며, " +
            "권한이 없는 경우 삭제가 거부됩니다. 삭제 후 원두 삭제 이벤트가 발행됩니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "원두 삭제 성공",
            content = @Content(
                schema = @Schema(implementation = BaseResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": true,
                            "message": "",
                            "data": {
                                "beanId": 1
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 - 존재하지 않는 원두 ID",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "원두를 찾을 수 없습니다. (id: 999)",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "권한 없음 - 다른 사용자의 원두 삭제 시도",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "해당 원두를 삭제할 권한이 없습니다. (id: 1)",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<BaseResponse<DeleteBeanResponse>> deleteBean(
        @Parameter(description = "삭제할 원두 ID", required = true, in = ParameterIn.PATH, example = "1")
        @PathVariable Long beanId
    );
}
