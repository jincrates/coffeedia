package io.coffeedia.application.usecase.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.coffeedia.application.usecase.dto.CreateRecipeCommand.CreateIngredientCommand;
import io.coffeedia.application.usecase.dto.UpdateRecipeCommand.UpdateIngredientCommand;
import io.coffeedia.domain.model.Ingredient;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RecipeMapperAmountValidationTest {

    @Nested
    @DisplayName("재료 수량 검증")
    class AmountValidationTest {

        @Test
        @DisplayName("유효한 BigDecimal 수량으로 재료 생성시 성공한다")
        void should_create_ingredient_when_valid_amount() {
            // given
            List<CreateIngredientCommand> commands = List.of(
                CreateIngredientCommand.builder()
                    .name("커피 원두")
                    .amount(new BigDecimal("18.5"))
                    .unit("g")
                    .build(),
                CreateIngredientCommand.builder()
                    .name("물")
                    .amount(new BigDecimal("200"))
                    .unit("ml")
                    .build()
            );

            // when
            List<Ingredient> ingredients = RecipeMapper.toIngredients(commands);

            // then
            assertThat(ingredients).hasSize(2);
            assertThat(ingredients.get(0).amount()).isEqualTo(new BigDecimal("18.5"));
            assertThat(ingredients.get(1).amount()).isEqualTo(new BigDecimal("200"));
        }

        @Test
        @DisplayName("null 수량으로 재료 생성시 IllegalArgumentException이 발생한다")
        void should_throw_exception_when_amount_is_null() {
            // given
            List<CreateIngredientCommand> commands = List.of(
                CreateIngredientCommand.builder()
                    .name("커피 원두")
                    .amount(null)
                    .unit("g")
                    .build()
            );

            // when & then
            assertThatThrownBy(() -> RecipeMapper.toIngredients(commands))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재료 '커피 원두'의 양은 필수입니다.");
        }

        @Test
        @DisplayName("0 이하의 수량으로 재료 생성시 IllegalArgumentException이 발생한다")
        void should_throw_exception_when_amount_is_zero_or_negative() {
            // given - 0인 경우
            List<CreateIngredientCommand> zeroCommands = List.of(
                CreateIngredientCommand.builder()
                    .name("커피 원두")
                    .amount(BigDecimal.ZERO)
                    .unit("g")
                    .build()
            );

            // when & then
            assertThatThrownBy(() -> RecipeMapper.toIngredients(zeroCommands))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재료 '커피 원두'의 양은 0보다 커야 합니다. 입력값: 0");

            // given - 음수인 경우
            List<CreateIngredientCommand> negativeCommands = List.of(
                CreateIngredientCommand.builder()
                    .name("설탕")
                    .amount(new BigDecimal("-1.5"))
                    .unit("g")
                    .build()
            );

            // when & then
            assertThatThrownBy(() -> RecipeMapper.toIngredients(negativeCommands))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재료 '설탕'의 양은 0보다 커야 합니다. 입력값: -1.5");
        }

        @Test
        @DisplayName("소수점 이하 3자리를 초과하는 수량으로 재료 생성시 IllegalArgumentException이 발생한다")
        void should_throw_exception_when_amount_has_more_than_3_decimal_places() {
            // given
            List<CreateIngredientCommand> commands = List.of(
                CreateIngredientCommand.builder()
                    .name("바닐라 시럽")
                    .amount(new BigDecimal("1.2345"))  // 소수점 이하 4자리
                    .unit("ml")
                    .build()
            );

            // when & then
            assertThatThrownBy(() -> RecipeMapper.toIngredients(commands))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재료 '바닐라 시럽'의 양은 소수점 이하 3자리까지만 입력 가능합니다. 입력값: 1.2345");
        }

        @Test
        @DisplayName("최대값을 초과하는 수량으로 재료 생성시 IllegalArgumentException이 발생한다")
        void should_throw_exception_when_amount_exceeds_maximum() {
            // given
            List<CreateIngredientCommand> commands = List.of(
                CreateIngredientCommand.builder()
                    .name("물")
                    .amount(new BigDecimal("1000000"))  // 999,999.999 초과
                    .unit("ml")
                    .build()
            );

            // when & then
            assertThatThrownBy(() -> RecipeMapper.toIngredients(commands))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재료 '물'의 양이 너무 큽니다. 최대 999,999.999까지 입력 가능합니다. 입력값: 1000000");
        }

        @Test
        @DisplayName("수정용 재료 명령에서도 동일한 검증이 적용된다")
        void should_validate_amount_for_update_commands() {
            // given
            List<UpdateIngredientCommand> commands = List.of(
                UpdateIngredientCommand.builder()
                    .name("에스프레소 원두")
                    .amount(new BigDecimal("18.75"))
                    .unit("g")
                    .build()
            );

            // when
            List<Ingredient> ingredients = RecipeMapper.toIngredients(commands);

            // then
            assertThat(ingredients).hasSize(1);
            assertThat(ingredients.get(0).amount()).isEqualTo(new BigDecimal("18.75"));
        }

        @Test
        @DisplayName("경계값 테스트 - 유효한 최대값과 소수점 3자리")
        void should_accept_boundary_values() {
            // given
            List<CreateIngredientCommand> commands = List.of(
                CreateIngredientCommand.builder()
                    .name("대용량 재료")
                    .amount(new BigDecimal("999999.999"))  // 최대값
                    .unit("g")
                    .build(),
                CreateIngredientCommand.builder()
                    .name("정밀 재료")
                    .amount(new BigDecimal("0.001"))  // 최소 유효값
                    .unit("g")
                    .build()
            );

            // when
            List<Ingredient> ingredients = RecipeMapper.toIngredients(commands);

            // then
            assertThat(ingredients).hasSize(2);
            assertThat(ingredients.get(0).amount()).isEqualTo(new BigDecimal("999999.999"));
            assertThat(ingredients.get(1).amount()).isEqualTo(new BigDecimal("0.001"));
        }
    }
}
