package io.coffeedia.application.usecase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import io.coffeedia.application.port.repository.RecipeRepositoryPort;
import io.coffeedia.application.usecase.DeleteRecipeUseCase;
import io.coffeedia.application.usecase.dto.DeleteRecipeCommand;
import io.coffeedia.application.usecase.dto.DeleteRecipeResponse;
import io.coffeedia.domain.exception.AccessDeniedException;
import io.coffeedia.domain.exception.RecipeNotFoundException;
import io.coffeedia.domain.model.Ingredient;
import io.coffeedia.domain.model.Recipe;
import io.coffeedia.domain.model.RecipeStep;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.domain.vo.CategoryType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteRecipeServiceTest {

    @Mock
    private RecipeRepositoryPort repository;

    private DeleteRecipeUseCase deleteRecipeUseCase;

    @BeforeEach
    void setUp() {
        deleteRecipeUseCase = new DeleteRecipeService(repository);
    }

    @Nested
    @DisplayName("레시피 삭제")
    class DeleteRecipeTest {

        @Test
        @DisplayName("유효한 커맨드로 레시피 삭제시 성공한다")
        void should_delete_recipe_when_valid_command() {
            // given
            Long recipeId = 1L;
            Long userId = 100L;
            Recipe existingRecipe = createSampleRecipe(recipeId, userId);
            DeleteRecipeCommand command = DeleteRecipeCommand.builder()
                .recipeId(recipeId)
                .userId(userId)
                .build();

            given(repository.findById(recipeId)).willReturn(Optional.of(existingRecipe));

            // when
            DeleteRecipeResponse response = deleteRecipeUseCase.invoke(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.recipeId()).isEqualTo(recipeId);

            then(repository).should(times(1)).findById(recipeId);
            then(repository).should(times(1)).deleteById(recipeId);
        }

        @Test
        @DisplayName("존재하지 않는 레시피 ID로 삭제시 RecipeNotFoundException이 발생한다")
        void should_throw_recipe_not_found_exception_when_recipe_not_exists() {
            // given
            Long nonExistentRecipeId = 999L;
            Long userId = 100L;
            DeleteRecipeCommand command = DeleteRecipeCommand.builder()
                .recipeId(nonExistentRecipeId)
                .userId(userId)
                .build();

            given(repository.findById(nonExistentRecipeId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> deleteRecipeUseCase.invoke(command))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessage("레시피를 찾을 수 없습니다. ID: " + nonExistentRecipeId);

            then(repository).should(times(1)).findById(nonExistentRecipeId);
            then(repository).should(times(0)).deleteById(any());
        }

        @Test
        @DisplayName("다른 사용자의 레시피 삭제시 AccessDeniedException이 발생한다")
        void should_throw_access_denied_exception_when_not_owner() {
            // given
            Long recipeId = 1L;
            Long ownerId = 100L;
            Long otherUserId = 200L;
            Recipe existingRecipe = createSampleRecipe(recipeId, ownerId);
            DeleteRecipeCommand command = DeleteRecipeCommand.builder()
                .recipeId(recipeId)
                .userId(otherUserId)
                .build();

            given(repository.findById(recipeId)).willReturn(Optional.of(existingRecipe));

            // when & then
            assertThatThrownBy(() -> deleteRecipeUseCase.invoke(command))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("레시피 삭제 권한이 없습니다. ID: " + recipeId);

            then(repository).should(times(1)).findById(recipeId);
            then(repository).should(times(0)).deleteById(any());
        }

        @Test
        @DisplayName("null 레시피 ID로 삭제시 IllegalArgumentException이 발생한다")
        void should_throw_exception_when_recipe_id_is_null() {
            // given
            DeleteRecipeCommand command = DeleteRecipeCommand.builder()
                .recipeId(null)
                .userId(100L)
                .build();

            // when & then
            assertThatThrownBy(() -> deleteRecipeUseCase.invoke(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 레시피 ID를 입력해주세요.");

            then(repository).should(times(0)).findById(any());
        }

        @Test
        @DisplayName("0 이하의 레시피 ID로 삭제시 IllegalArgumentException이 발생한다")
        void should_throw_exception_when_recipe_id_is_invalid() {
            // when & then
            assertThatThrownBy(() -> deleteRecipeUseCase.invoke(
                DeleteRecipeCommand.builder()
                    .recipeId(0L)
                    .userId(100L)
                    .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 레시피 ID를 입력해주세요.");

            assertThatThrownBy(() -> deleteRecipeUseCase.invoke(
                DeleteRecipeCommand.builder()
                    .recipeId(-1L)
                    .userId(100L)
                    .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 레시피 ID를 입력해주세요.");
        }

        @Test
        @DisplayName("null 사용자 ID로 삭제시 IllegalArgumentException이 발생한다")
        void should_throw_exception_when_user_id_is_null() {
            // given
            DeleteRecipeCommand command = DeleteRecipeCommand.builder()
                .recipeId(1L)
                .userId(null)
                .build();

            // when & then
            assertThatThrownBy(() -> deleteRecipeUseCase.invoke(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 사용자 ID를 입력해주세요.");
        }

        @Test
        @DisplayName("0 이하의 사용자 ID로 삭제시 IllegalArgumentException이 발생한다")
        void should_throw_exception_when_user_id_is_invalid() {
            // when & then
            assertThatThrownBy(() -> deleteRecipeUseCase.invoke(
                DeleteRecipeCommand.builder()
                    .recipeId(1L)
                    .userId(0L)
                    .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 사용자 ID를 입력해주세요.");

            assertThatThrownBy(() -> deleteRecipeUseCase.invoke(
                DeleteRecipeCommand.builder()
                    .recipeId(1L)
                    .userId(-1L)
                    .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 사용자 ID를 입력해주세요.");
        }

        @Test
        @DisplayName("레시피 소유자와 삭제 요청자가 일치하는 경우 삭제가 성공한다")
        void should_delete_recipe_when_owner_matches() {
            // given
            Long recipeId = 1L;
            Long userId = 100L;
            Recipe existingRecipe = createSampleRecipe(recipeId, userId);
            DeleteRecipeCommand command = DeleteRecipeCommand.builder()
                .recipeId(recipeId)
                .userId(userId)
                .build();

            given(repository.findById(recipeId)).willReturn(Optional.of(existingRecipe));

            // when
            DeleteRecipeResponse response = deleteRecipeUseCase.invoke(command);

            // then
            assertThat(response).isNotNull();
            then(repository).should(times(1)).deleteById(recipeId);
        }

        @Test
        @DisplayName("삭제 후 응답에 삭제된 레시피 ID가 포함된다")
        void should_return_deleted_recipe_id() {
            // given
            Long recipeId = 1L;
            Long userId = 100L;
            Recipe existingRecipe = createSampleRecipe(recipeId, userId);
            DeleteRecipeCommand command = DeleteRecipeCommand.builder()
                .recipeId(recipeId)
                .userId(userId)
                .build();

            given(repository.findById(recipeId)).willReturn(Optional.of(existingRecipe));

            // when
            DeleteRecipeResponse response = deleteRecipeUseCase.invoke(command);

            // then
            assertThat(response.recipeId()).isEqualTo(recipeId);
        }

        @Test
        @DisplayName("삭제 권한 검증이 repository 조회 후에 수행된다")
        void should_validate_ownership_after_repository_lookup() {
            // given
            Long recipeId = 1L;
            Long ownerId = 100L;
            Long otherUserId = 200L;
            Recipe existingRecipe = createSampleRecipe(recipeId, ownerId);
            DeleteRecipeCommand command = DeleteRecipeCommand.builder()
                .recipeId(recipeId)
                .userId(otherUserId)
                .build();

            given(repository.findById(recipeId)).willReturn(Optional.of(existingRecipe));

            // when & then
            assertThatThrownBy(() -> deleteRecipeUseCase.invoke(command))
                .isInstanceOf(AccessDeniedException.class);

            // repository 조회는 수행되었지만 삭제는 수행되지 않음
            then(repository).should(times(1)).findById(recipeId);
            then(repository).should(times(0)).deleteById(any());
        }
    }

    private Recipe createSampleRecipe(Long recipeId, Long userId) {
        return Recipe.builder()
            .id(recipeId)
            .userId(userId)
            .category(CategoryType.ESPRESSO)
            .title("테스트 레시피")
            .thumbnailUrl("https://example.com/thumbnail.jpg")
            .description("테스트 레시피 설명")
            .serving(1)
            .tags(Arrays.asList("테스트", "에스프레소"))
            .ingredients(Collections.singletonList(
                Ingredient.builder()
                    .name("에스프레소 원두")
                    .amount(new BigDecimal("18"))
                    .unit("g")
                    .build()
            ))
            .steps(Arrays.asList(
                RecipeStep.builder()
                    .sortOrder(1)
                    .description("원두를 분쇄합니다.")
                    .build()
            ))
            .tips("테스트 팁")
            .status(ActiveStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}
