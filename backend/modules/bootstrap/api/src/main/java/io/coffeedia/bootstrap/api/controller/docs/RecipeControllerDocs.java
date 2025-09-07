package io.coffeedia.bootstrap.api.controller.docs;

import io.coffeedia.application.usecase.dto.CreateRecipeCommand;
import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.application.usecase.dto.RecipeSummaryResponse;
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

@Tag(name = "레시피 관리", description = "커피 레시피 등록, 조회, 목록 조회 API")
public interface RecipeControllerDocs {

    @Operation(
        summary = "레시피 등록",
        description = "새로운 커피 레시피를 등록합니다. 카테고리, 제목, 인분, 재료, 제조 단계는 필수이며, " +
            "썸네일, 설명, 태그, 팁 등을 추가로 설정할 수 있습니다. 태그는 최대 5개까지 등록 가능합니다."
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
                        name = "태그 개수 초과",
                        value = """
                            {
                                "success": false,
                                "message": "태그는 최대 5개까지만 가능합니다.",
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
        description = "등록된 레시피 목록을 페이징과 정렬을 통해 조회합니다. " +
            "기본적으로 페이지당 10개씩 조회되며, 생성일시 기준으로 내림차순 정렬됩니다.",
        parameters = {
            @Parameter(
                name = "page",
                description = "페이지 번호 (0부터 시작)",
                example = "0",
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "size",
                description = "페이지당 조회할 레시피 개수",
                example = "10",
                in = ParameterIn.QUERY
            ),
            @Parameter(
                name = "sort",
                description = "정렬 조건 (예: 'createdAt:desc', 'title:asc,createdAt:desc')",
                example = "createdAt:desc",
                in = ParameterIn.QUERY
            )
        }
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
        description = "레시피 ID로 상세 정보를 조회합니다. 레시피의 모든 정보(재료, 제조 단계, 팁 등)를 확인할 수 있습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "레시피 상세 조회 성공",
            content = @Content(
                schema = @Schema(implementation = RecipeResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                            "success": true,
                            "message": "요청이 성공했습니다.",
                            "data": {
                                "id": 1,
                                "userId": 100,
                                "category": "ESPRESSO",
                                "title": "진한 에스프레소 레시피",
                                "thumbnailUrl": "https://example.com/thumbnail.jpg",
                                "description": "진한 에스프레소를 만드는 기본 레시피입니다.",
                                "serving": 1,
                                "tags": ["에스프레소", "기본", "강함"],
                                "ingredients": [
                                    {
                                        "name": "에스프레소 원두",
                                        "amount": "18",
                                        "unit": "g",
                                        "buyUrl": "https://example.com/beans"
                                    }
                                ],
                                "steps": [
                                    {
                                        "sortOrder": 1,
                                        "description": "그라인더로 원두를 분쇄합니다.",
                                        "imageUrl": "https://example.com/step1.jpg"
                                    }
                                ],
                                "tips": "온도를 90-96도로 유지하세요.",
                                "status": "ACTIVE",
                                "createdAt": "2024-01-01 12:00:00",
                                "updatedAt": "2024-01-01 12:00:00"
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 레시피 ID",
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
        @Parameter(description = "조회할 레시피 ID", required = true, in = ParameterIn.PATH, example = "1")
        @PathVariable Long recipeId
    );
}
