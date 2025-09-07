package io.coffeedia.application.usecase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import io.coffeedia.application.port.repository.RecipeRepositoryPort;
import io.coffeedia.application.usecase.GetRecipeUseCase;
import io.coffeedia.application.usecase.dto.RecipeResponse;
import io.coffeedia.domain.exception.RecipeNotFoundException;
import io.coffeedia.domain.model.Ingredient;
import io.coffeedia.domain.model.Recipe;
import io.coffeedia.domain.model.RecipeStep;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.CategoryType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetRecipeServiceTest {

    @Mock
    private RecipeRepositoryPort repository;

    private GetRecipeUseCase getRecipeUseCase;

    @BeforeEach
    void setUp() {
        getRecipeUseCase = new GetRecipeService(repository);
    }

    @Nested
    @DisplayName("레시피 상세 조회")
    class GetRecipeTest {

        @Test
        @DisplayName("유효한 레시피 ID로 조회시 레시피 상세 정보를 반환한다")
        void should_return_recipe_detail_when_valid_recipe_id() {
            // given
            Long recipeId = 1L;
            Recipe recipe = createSampleRecipe(recipeId);

            given(repository.findById(recipeId)).willReturn(Optional.of(recipe));

            // when
            RecipeResponse response = getRecipeUseCase.invoke(recipeId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(recipeId);
            assertThat(response.title()).isEqualTo("에스프레소 레시피");
            assertThat(response.category()).isEqualTo(CategoryType.ESPRESSO);
            assertThat(response.userId()).isEqualTo(100L);
            assertThat(response.serving()).isEqualTo(1);
            assertThat(response.ingredients()).hasSize(2);
            assertThat(response.steps()).hasSize(3);
            assertThat(response.status()).isEqualTo(ActiveStatus.ACTIVE);
        }

        @Test
        @DisplayName("존재하지 않는 레시피 ID로 조회시 RecipeNotFoundException이 발생한다")
        void should_throw_recipe_not_found_exception_when_recipe_not_exists() {
            // given
            Long nonExistentRecipeId = 999L;
            given(repository.findById(nonExistentRecipeId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> getRecipeUseCase.invoke(nonExistentRecipeId))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessage("레시피를 찾을 수 없습니다. ID: " + nonExistentRecipeId);
        }

        @Test
        @DisplayName("null 레시피 ID로 조회시 IllegalArgumentException이 발생한다")
        void should_throw_illegal_argument_exception_when_recipe_id_is_null() {
            // when & then
            assertThatThrownBy(() -> getRecipeUseCase.invoke(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 레시피 ID를 입력해주세요.");
        }

        @Test
        @DisplayName("0 이하의 레시피 ID로 조회시 IllegalArgumentException이 발생한다")
        void should_throw_illegal_argument_exception_when_recipe_id_is_invalid() {
            // when & then
            assertThatThrownBy(() -> getRecipeUseCase.invoke(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 레시피 ID를 입력해주세요.");

            assertThatThrownBy(() -> getRecipeUseCase.invoke(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 레시피 ID를 입력해주세요.");
        }
    }

    private Recipe createSampleRecipe(Long recipeId) {
        List<Ingredient> ingredients = List.of(
            Ingredient.builder()
                .name("에스프레소 원두")
                .amount(BigDecimal.valueOf(18))
                .unit("g")
                .buyUrl("https://example.com/beans")
                .build(),
            Ingredient.builder()
                .name("물")
                .amount(BigDecimal.valueOf(36))
                .unit("ml")
                .buyUrl(null)
                .build()
        );

        List<RecipeStep> steps = List.of(
            RecipeStep.builder()
                .sortOrder(1)
                .description("그라인더로 원두를 분쇄합니다.")
                .imageUrl("https://example.com/step1.jpg")
                .build(),
            RecipeStep.builder()
                .sortOrder(2)
                .description("포타필터에 원두를 담고 탬핑합니다.")
                .imageUrl("https://example.com/step2.jpg")
                .build(),
            RecipeStep.builder()
                .sortOrder(3)
                .description("25-30초간 추출합니다.")
                .imageUrl("https://example.com/step3.jpg")
                .build()
        );

        return Recipe.builder()
            .id(recipeId)
            .userId(100L)
            .category(CategoryType.ESPRESSO)
            .title("에스프레소 레시피")
            .thumbnailUrl("https://example.com/thumbnail.jpg")
            .description("진한 에스프레소를 만드는 기본 레시피입니다.")
            .serving(1)
            .tags(List.of("에스프레소", "기본", "강함"))
            .ingredients(ingredients)
            .steps(steps)
            .tips("온도를 90-96도로 유지하세요.")
            .status(ActiveStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}
