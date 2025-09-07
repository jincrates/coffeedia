package io.coffeedia.bootstrap.api.controller.docs;

import io.coffeedia.application.usecase.dto.CreateRecipeCommand;
import io.coffeedia.application.usecase.dto.DeleteRecipeResponse;
import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.application.usecase.dto.RecipeSummaryResponse;
import io.coffeedia.application.usecase.dto.UpdateRecipeCommand;
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

@Tag(name = "레시피 관리", description = "커피 레시피 등록, 조회, 수정, 삭제 API")
public interface RecipeControllerDocs {

    @Operation(
        summary = "레시피 등록",
        description = "새로운 커피 레시피를 등록합니다. 카테고리, 제목, 인분, 재료, 제조 단계는 필수이며, " +
            "썸네일, 설명, 태그, 팁 등을 추가로 설정할 수 있습니다. 태그는 최대 5개까지 등록 가능합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                examples = {
                    @ExampleObject(
                        name = "에스프레소 레시피",
                        summary = "기본 에스프레소 레시피 등록",
                        value = """
                            {
                                "category": "ESPRESSO",
                                "title": "진한 에스프레소 레시피",
                                "thumbnailUrl": "https://example.com/espresso-thumbnail.jpg",
                                "description": "진한 에스프레소를 만드는 기본 레시피입니다.",
                                "serving": 1,
                                "tags": ["에스프레소", "기본", "강함"],
                                "ingredients": [
                                    {
                                        "name": "에스프레소 원두",
                                        "amount": 18.0,
                                        "unit": "g",
                                        "buyUrl": "https://example.com/beans"
                                    },
                                    {
                                        "name": "물",
                                        "amount": 36.0,
                                        "unit": "ml"
                                    }
                                ],
                                "steps": [
                                    {
                                        "description": "그라인더로 원두를 분쇄합니다.",
                                        "imageUrl": "https://example.com/step1.jpg"
                                    },
                                    {
                                        "description": "포타필터에 원두를 담고 탬핑합니다.",
                                        "imageUrl": "https://example.com/step2.jpg"
                                    }
                                ],
                                "tips": "온도를 90-96도로 유지하세요."
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "드립 커피 레시피",
                        summary = "핸드드립 커피 레시피 등록",
                        value = """
                            {
                                "category": "DRIP",
                                "title": "V60 핸드드립 레시피",
                                "thumbnailUrl": "https://example.com/v60-thumbnail.jpg",
                                "description": "V60을 이용한 깔끔한 핸드드립 커피 레시피입니다.",
                                "serving": 2,
                                "tags": ["핸드드립", "V60", "산미"],
                                "ingredients": [
                                    {
                                        "name": "원두",
                                        "amount": 20.0,
                                        "unit": "g"
                                    },
                                    {
                                        "name": "물",
                                        "amount": 300.0,
                                        "unit": "ml"
                                    }
                                ],
                                "steps": [
                                    {
                                        "description": "필터를 린싱합니다."
                                    },
                                    {
                                        "description": "30초간 블루밍합니다."
                                    }
                                ],
                                "tips": "물 온도는 92-96도가 적당합니다."
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "레시피 등록 성공",
            content = @Content(schema = @Schema(implementation = RecipeResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(
                examples = {
                    @ExampleObject(
                        name = "필수 필드 누락",
                        value = """
                            {
                                "success": false,
                                "message": "제목은 필수입니다.",
                                "data": null
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "재료 수량 오류",
                        value = """
                            {
                                "success": false,
                                "message": "재료 '설탕'의 양은 0보다 커야 합니다. 입력값: -1.5",
                                "data": null
                            }
                            """
                    )
                }
            )
        )
    })
    ResponseEntity<BaseResponse<RecipeResponse>> createRecipe(
        @Parameter(description = "레시피 등록 요청 데이터", required = true)
        @Valid @RequestBody CreateRecipeCommand command
    );

    @Operation(
        summary = "레시피 목록 조회",
        description = "등록된 레시피 목록을 페이징과 정렬을 통해 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "레시피 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = PageResponse.class))
        )
    })
    ResponseEntity<BaseResponse<PageResponse<RecipeSummaryResponse>>> getAllRecipeSummaries(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort
    );

    @Operation(
        summary = "레시피 상세 조회",
        description = "레시피 ID로 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "레시피 상세 조회 성공",
            content = @Content(schema = @Schema(implementation = RecipeResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "레시피를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "레시피를 찾을 수 없습니다. ID: 999",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<BaseResponse<RecipeResponse>> getRecipe(
        @Parameter(description = "조회할 레시피 ID", required = true, example = "1")
        @PathVariable Long recipeId
    );

    @Operation(
        summary = "레시피 수정",
        description = "기존 레시피의 정보를 수정합니다. 수정하고자 하는 필드만 전송하면 되며, " +
            "전송되지 않은 필드는 기존 값이 유지됩니다. 본인이 작성한 레시피만 수정할 수 있습니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                examples = {
                    @ExampleObject(
                        name = "제목만 수정",
                        summary = "레시피 제목만 변경",
                        value = """
                            {
                                "title": "수정된 에스프레소 레시피"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "재료 수정",
                        summary = "재료 목록 전체 변경",
                        value = """
                            {
                                "ingredients": [
                                    {
                                        "name": "에스프레소 원두",
                                        "amount": 20.0,
                                        "unit": "g",
                                        "buyUrl": "https://example.com/premium-beans"
                                    },
                                    {
                                        "name": "물",
                                        "amount": 40.0,
                                        "unit": "ml"
                                    }
                                ]
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "여러 필드 수정",
                        summary = "제목, 설명, 상태 등 여러 필드 한번에 수정",
                        value = """
                            {
                                "title": "개선된 에스프레소 레시피",
                                "description": "더욱 진하고 깊은 맛의 에스프레소 레시피입니다.",
                                "serving": 2,
                                "tags": ["에스프레소", "진한맛", "프리미엄"],
                                "tips": "온도를 90-96도로 유지하고, 추출 시간은 25초가 최적입니다."
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "레시피 수정 성공",
            content = @Content(schema = @Schema(implementation = RecipeResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "올바른 레시피 ID를 입력해주세요.",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "수정 권한 없음",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "레시피 수정 권한이 없습니다. ID: 1",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "레시피를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "레시피를 찾을 수 없습니다. ID: 999",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<BaseResponse<RecipeResponse>> updateRecipe(
        @Parameter(description = "수정할 레시피 ID", required = true, example = "1")
        @PathVariable Long recipeId,
        @Parameter(description = "레시피 수정 요청 데이터", required = true)
        @Valid @RequestBody UpdateRecipeCommand command
    );

    @Operation(
        summary = "레시피 삭제",
        description = "특정 레시피를 삭제합니다. 본인이 작성한 레시피만 삭제할 수 있습니다. " +
            "삭제된 레시피는 복구할 수 없으므로 신중하게 사용해주세요."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "레시피 삭제 성공",
            content = @Content(
                schema = @Schema(implementation = DeleteRecipeResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": true,
                            "message": "삭제가 완료되었습니다.",
                            "data": {
                                "recipeId": 1
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "올바른 레시피 ID를 입력해주세요.",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "삭제 권한 없음",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "레시피 삭제 권한이 없습니다. ID: 1",
                            "data": null
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "레시피를 찾을 수 없음",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                            "success": false,
                            "message": "레시피를 찾을 수 없습니다. ID: 999",
                            "data": null
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<BaseResponse<DeleteRecipeResponse>> deleteRecipe(
        @Parameter(description = "삭제할 레시피 ID", required = true, example = "1")
        @PathVariable Long recipeId
    );
}
