package io.coffeedia.bootstrap.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.coffeedia.IntegrationSupportTest;
import io.coffeedia.application.usecase.dto.CreateRecipeCommand;
import io.coffeedia.application.usecase.dto.CreateRecipeCommand.CreateIngredientCommand;
import io.coffeedia.application.usecase.dto.CreateRecipeCommand.CreateStepCommand;
import io.coffeedia.application.usecase.dto.DeleteRecipeResponse;
import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.application.usecase.dto.RecipeSummaryResponse;
import io.coffeedia.application.usecase.dto.UpdateRecipeCommand;
import io.coffeedia.bootstrap.api.controller.dto.BaseResponse;
import io.coffeedia.bootstrap.api.controller.dto.PageResponse;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.CategoryType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

class RecipeControllerTest extends IntegrationSupportTest {

    @BeforeEach
    void setUp() {
        cleanUpRecipes();
    }

    @Nested
    @DisplayName("레시피 등록")
    class CreateRecipeTest {

        @Test
        @DisplayName("필수 정보만으로도 레시피가 정상적으로 등록된다")
        void createRecipeWithRequiredFieldsOnly() {
            // given
            CreateRecipeCommand command = CreateRecipeCommand.builder()
                .category(CategoryType.HAND_DRIP)
                .title("V60 핸드드립")
                .serving(1)
                .ingredients(List.of(
                    CreateIngredientCommand.builder()
                        .name("원두")
                        .amount(BigDecimal.valueOf(20))
                        .unit("g")
                        .build(),
                    CreateIngredientCommand.builder()
                        .name("물")
                        .amount(BigDecimal.valueOf(300))
                        .unit("ml")
                        .build()
                ))
                .steps(List.of(
                    CreateStepCommand.builder()
                        .description("원두를 중간 굵기로 분쇄합니다.")
                        .build(),
                    CreateStepCommand.builder()
                        .description("90도 물을 준비합니다.")
                        .build()
                ))
                .build();

            // when & then
            authenticatedPost("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<BaseResponse<RecipeResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();

                    RecipeResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.title()).isEqualTo(command.title()),
                        () -> assertThat(data.category()).isEqualTo(command.category()),
                        () -> assertThat(data.serving()).isEqualTo(command.serving()),
                        () -> assertThat(data.ingredients()).hasSize(2),
                        () -> assertThat(data.steps()).hasSize(2),
                        () -> assertThat(data.status()).isEqualTo(ActiveStatus.ACTIVE),
                        () -> assertThat(data.createdAt()).isNotNull(),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("인증 없이 레시피 등록 시 401 Unauthorized를 반환한다")
        void createRecipeWithoutAuthenticationReturns401() {
            // given
            CreateRecipeCommand command = CreateRecipeCommand.builder()
                .category(CategoryType.HAND_DRIP)
                .title("V60 핸드드립")
                .serving(1)
                .ingredients(List.of(
                    CreateIngredientCommand.builder()
                        .name("원두")
                        .amount(BigDecimal.valueOf(20))
                        .unit("g")
                        .build()
                ))
                .steps(List.of(
                    CreateStepCommand.builder()
                        .description("원두를 분쇄합니다.")
                        .build()
                ))
                .build();

            // when & then
            webTestClient.post()
                .uri("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("모든 레시피 정보를 입력하면 완전한 레시피가 생성된다")
        void createRecipeWithAllFields() {
            // given
            CreateRecipeCommand command = CreateRecipeCommand.builder()
                .category(CategoryType.ESPRESSO)
                .title("클래식 에스프레소")
                .thumbnailUrl("https://example.com/espresso.jpg")
                .description("진한 맛의 전통적인 에스프레소")
                .serving(1)
                .tags(List.of("진한맛", "쌉쌀한"))
                .ingredients(List.of(
                    CreateIngredientCommand.builder()
                        .name("원두")
                        .amount(BigDecimal.valueOf(18))
                        .unit("g")
                        .buyUrl("https://example.com/beans")
                        .build()
                ))
                .steps(List.of(
                    CreateStepCommand.builder()
                        .imageUrl("https://example.com/grind.jpg")
                        .description("원두를 에스프레소용으로 곱게 분쇄합니다.")
                        .build(),
                    CreateStepCommand.builder()
                        .imageUrl("https://example.com/extract.jpg")
                        .description("25-30초간 추출합니다.")
                        .build()
                ))
                .tips("추출 시간은 25-30초, 압력은 9바가 이상적입니다.")
                .build();

            // when & then
            authenticatedPost("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<BaseResponse<RecipeResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    RecipeResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.title()).isEqualTo(command.title()),
                        () -> assertThat(data.category()).isEqualTo(command.category()),
                        () -> assertThat(data.thumbnailUrl()).isEqualTo(command.thumbnailUrl()),
                        () -> assertThat(data.description()).isEqualTo(command.description()),
                        () -> assertThat(data.serving()).isEqualTo(command.serving()),
                        () -> assertThat(data.tags()).containsExactlyInAnyOrderElementsOf(
                            command.tags()),
                        () -> assertThat(data.ingredients()).hasSize(1),
                        () -> assertThat(data.steps()).hasSize(2),
                        () -> assertThat(data.tips()).isEqualTo(command.tips()),
                        () -> assertThat(data.status()).isEqualTo(ActiveStatus.ACTIVE)
                    );
                });
        }

        @Test
        @DisplayName("레시피 제목이 없으면 등록이 거부된다")
        void rejectRecipeCreationWhenTitleIsEmpty() {
            // given
            CreateRecipeCommand command = CreateRecipeCommand.builder()
                .category(CategoryType.HAND_DRIP)
                .title("")
                .serving(1)
                .ingredients(List.of(
                    CreateIngredientCommand.builder()
                        .name("원두")
                        .amount(BigDecimal.valueOf(20))
                        .unit("g")
                        .build()
                ))
                .steps(List.of(
                    CreateStepCommand.builder()
                        .description("원두를 분쇄합니다.")
                        .build()
                ))
                .build();

            // when & then
            authenticatedPost("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("제목은 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("레시피 카테고리가 누락되면 등록이 거부된다")
        void rejectRecipeCreationWhenCategoryIsNull() {
            // given
            CreateRecipeCommand command = CreateRecipeCommand.builder()
                .category(null)
                .title("테스트 레시피")
                .serving(1)
                .ingredients(List.of(
                    CreateIngredientCommand.builder()
                        .name("원두")
                        .amount(BigDecimal.valueOf(20))
                        .unit("g")
                        .build()
                ))
                .steps(List.of(
                    CreateStepCommand.builder()
                        .description("원두를 분쇄합니다.")
                        .build()
                ))
                .build();

            // when & then
            authenticatedPost("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("카테고리는 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("인분이 0 이하이면 등록이 거부된다")
        void rejectRecipeCreationWhenServingIsZeroOrNegative() {
            // given
            CreateRecipeCommand command = CreateRecipeCommand.builder()
                .category(CategoryType.HAND_DRIP)
                .title("테스트 레시피")
                .serving(0)
                .ingredients(List.of(
                    CreateIngredientCommand.builder()
                        .name("원두")
                        .amount(BigDecimal.valueOf(20))
                        .unit("g")
                        .build()
                ))
                .steps(List.of(
                    CreateStepCommand.builder()
                        .description("원두를 분쇄합니다.")
                        .build()
                ))
                .build();

            // when & then
            authenticatedPost("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("1인분 이상이어야 합니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("재료가 없으면 등록이 거부된다")
        void rejectRecipeCreationWhenIngredientsIsEmpty() {
            // given
            CreateRecipeCommand command = CreateRecipeCommand.builder()
                .category(CategoryType.HAND_DRIP)
                .title("테스트 레시피")
                .serving(1)
                .ingredients(List.of())
                .steps(List.of(
                    CreateStepCommand.builder()
                        .description("원두를 분쇄합니다.")
                        .build()
                ))
                .build();

            // when & then
            authenticatedPost("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("레시피 재료는 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("단계가 없으면 등록이 거부된다")
        void rejectRecipeCreationWhenStepsIsEmpty() {
            // given
            CreateRecipeCommand command = CreateRecipeCommand.builder()
                .category(CategoryType.HAND_DRIP)
                .title("테스트 레시피")
                .serving(1)
                .ingredients(List.of(
                    CreateIngredientCommand.builder()
                        .name("원두")
                        .amount(BigDecimal.valueOf(20))
                        .unit("g")
                        .build()
                ))
                .steps(List.of())
                .build();

            // when & then
            authenticatedPost("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("레시피 단계는 필수입니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("태그가 5개를 초과하면 등록이 거부된다")
        void rejectRecipeCreationWhenTagsExceedLimit() {
            // given
            CreateRecipeCommand command = CreateRecipeCommand.builder()
                .category(CategoryType.HAND_DRIP)
                .title("테스트 레시피")
                .serving(1)
                .tags(List.of("태그1", "태그2", "태그3", "태그4", "태그5", "태그6"))
                .ingredients(List.of(
                    CreateIngredientCommand.builder()
                        .name("원두")
                        .amount(BigDecimal.valueOf(20))
                        .unit("g")
                        .build()
                ))
                .steps(List.of(
                    CreateStepCommand.builder()
                        .description("원두를 분쇄합니다.")
                        .build()
                ))
                .build();

            // when & then
            authenticatedPost("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("태그는 최대 5개까지만 가능합니다");
                    assertThat(response.data()).isNull();
                });
        }

        @Test
        @DisplayName("콜드브루 레시피도 정상적으로 등록된다")
        void createColdBrewRecipeSuccessfully() {
            // given
            CreateRecipeCommand command = CreateRecipeCommand.builder()
                .category(CategoryType.COLD_BREW)
                .title("시원한 콜드브루")
                .serving(4)
                .tags(List.of("부드러운", "달콤한"))
                .ingredients(List.of(
                    CreateIngredientCommand.builder()
                        .name("원두")
                        .amount(BigDecimal.valueOf(100))
                        .unit("g")
                        .build(),
                    CreateIngredientCommand.builder()
                        .name("찬물")
                        .amount(BigDecimal.valueOf(1000))
                        .unit("ml")
                        .build()
                ))
                .steps(List.of(
                    CreateStepCommand.builder()
                        .description("원두를 굵게 분쇄합니다.")
                        .build(),
                    CreateStepCommand.builder()
                        .description("찬물과 함께 12-24시간 우립니다.")
                        .build()
                ))
                .tips("우리는 시간이 길수록 진한 맛이 납니다.")
                .build();

            // when & then
            authenticatedPost("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<BaseResponse<RecipeResponse>>() {
                })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    RecipeResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.category()).isEqualTo(CategoryType.COLD_BREW),
                        () -> assertThat(data.title()).isEqualTo(command.title()),
                        () -> assertThat(data.serving()).isEqualTo(4),
                        () -> assertThat(data.tags()).containsExactlyInAnyOrder("부드러운", "달콤한"),
                        () -> assertThat(data.ingredients()).hasSize(2),
                        () -> assertThat(data.steps()).hasSize(2),
                        () -> assertThat(data.tips()).isEqualTo(command.tips())
                    );
                });
        }
    }

    @Nested
    @DisplayName("레시피 목록 조회")
    class GetAllRecipesTest {

        @BeforeEach
        void setUp() {
            createRecipes(5);
        }

        @Test
        @DisplayName("기본 페이징으로 레시피 목록을 조회한다")
        void getAllRecipesWithDefaultPaging() {
            // when & then
            authenticatedGet("/api/recipes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<RecipeSummaryResponse>>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.message()).isBlank();
                    assertThat(response.data()).isNotNull();

                    PageResponse<RecipeSummaryResponse> pageData = response.data();
                    assertAll(
                        () -> assertThat(pageData.page()).isEqualTo(0),
                        () -> assertThat(pageData.content()).hasSize(5)
                    );
                });
        }

        @Test
        @DisplayName("인증 없이 레시피 목록 조회 시 401 Unauthorized를 반환한다")
        void getAllRecipesWithoutAuthenticationReturns401() {
            // when & then
            webTestClient.get()
                .uri("/api/recipes")
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("페이지 크기를 지정하여 레시피 목록을 조회한다")
        void getAllRecipesWithCustomPageSize() {
            // when & then
            authenticatedGet("/api/recipes?page=0&size=2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<RecipeSummaryResponse>>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();

                    PageResponse<RecipeSummaryResponse> pageData = response.data();
                    assertAll(
                        () -> assertThat(pageData.page()).isEqualTo(0),
                        () -> assertThat(pageData.content()).hasSize(2)
                    );
                });
        }

        @Test
        @DisplayName("정렬 조건을 지정하여 레시피 목록을 조회한다")
        void getAllRecipesWithSorting() {
            // when & then
            authenticatedGet("/api/recipes?sort=createdAt:desc")
                .exchange()
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<RecipeSummaryResponse>>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();
                    assertThat(response.data().content()).isNotEmpty();
                });
        }

        @Test
        @DisplayName("레시피가 없을 때 빈 목록을 반환한다")
        void returnEmptyListWhenNoRecipesExist() {
            cleanUpRecipes();

            // when & then
            authenticatedGet("/api/recipes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<RecipeSummaryResponse>>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();
                    assertThat(response.data().content()).isEmpty();
                });
        }

        @Test
        @DisplayName("페이지 번호가 범위를 벗어나도 빈 목록을 정상 반환한다")
        void returnEmptyListWhenPageOutOfRange() {
            // when & then
            authenticatedGet("/api/recipes?page=10&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(
                    new ParameterizedTypeReference<BaseResponse<PageResponse<RecipeSummaryResponse>>>() {
                    })
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();
                    assertThat(response.data().content()).isEmpty();
                });
        }
    }

    @Nested
    @DisplayName("레시피 수정")
    class UpdateRecipeTest {

        private Long recipeId;

        @BeforeEach
        void setUp() {
            recipeId = createRecipe().id();
        }

        @Test
        @DisplayName("레시피를 정상적으로 수정할 수 있다")
        void updateRecipeSuccessfully() {
            // given
            UpdateRecipeCommand command = UpdateRecipeCommand.builder()
                .title("수정된 레시피 제목")
                .description("수정된 설명")
                .serving(2)
                .build();

            // when & then
            authenticatedPut("/api/recipes/{recipeId}", recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<RecipeResponse>>() {
                })
                .value(response -> {
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();
                    RecipeResponse data = response.data();
                    assertAll(
                        () -> assertThat(data.title()).isEqualTo("수정된 레시피 제목"),
                        () -> assertThat(data.description()).isEqualTo("수정된 설명"),
                        () -> assertThat(data.serving()).isEqualTo(2),
                        () -> assertThat(data.updatedAt()).isNotNull()
                    );
                });
        }

        @Test
        @DisplayName("인증 없이 레시피 수정 시 401 Unauthorized를 반환한다")
        void updateRecipeWithoutAuthenticationReturns401() {
            // given
            UpdateRecipeCommand command = UpdateRecipeCommand.builder()
                .title("수정된 레시피 제목")
                .build();

            // when & then
            webTestClient.put()
                .uri("/api/recipes/{recipeId}", recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("존재하지 않는 레시피를 수정하면 실패한다")
        void failToUpdateNonExistentRecipe() {
            // given
            UpdateRecipeCommand command = UpdateRecipeCommand.builder()
                .title("없는 레시피")
                .build();

            // when & then
            authenticatedPut("/api/recipes/{recipeId}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("레시피를 찾을 수 없습니다. ID: 99999");
                });
        }
    }

    @Nested
    @DisplayName("레시피 삭제")
    class DeleteRecipeTest {

        private Long recipeId;

        @BeforeEach
        void setUp() {
            // 테스트용 레시피 하나 등록 후 ID 확보
            recipeId = createRecipe().id();
        }

        @Test
        @DisplayName("레시피를 정상적으로 삭제할 수 있다")
        void deleteRecipeSuccessfully() {
            // when & then
            authenticatedDelete("/api/recipes/{recipeId}", recipeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<BaseResponse<DeleteRecipeResponse>>() {
                })
                .value(response -> {
                    assertThat(response.success()).isTrue();
                    assertThat(response.data()).isNotNull();
                    assertThat(response.data().recipeId()).isEqualTo(recipeId);
                });
        }

        @Test
        @DisplayName("인증 없이 레시피 삭제 시 401 Unauthorized를 반환한다")
        void deleteRecipeWithoutAuthenticationReturns401() {
            // when & then
            webTestClient.delete()
                .uri("/api/recipes/{recipeId}", recipeId)
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("존재하지 않는 레시피를 삭제하면 실패한다")
        void failToDeleteNonExistentRecipe() {
            // when & then
            authenticatedDelete("/api/recipes/{recipeId}", 99999L)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<BaseResponse<Void>>() {
                })
                .value(response -> {
                    assertThat(response.success()).isFalse();
                    assertThat(response.message()).contains("레시피를 찾을 수 없습니다. ID: 99999");
                });
        }
    }
}
