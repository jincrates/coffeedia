package io.coffeedia.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ValueUtilTest {

    @Test
    @DisplayName("새 값이 null이면 기존 값을 반환한다")
    void whenNewValueIsNull_thenExistingValueIsReturned() {
        String existingValue = "existing";
        String result = ValueUtil.defaultIfNull(
            null,
            existingValue
        );

        assertThat(result).isEqualTo("existing");
    }

    @Test
    @DisplayName("새 값이 null이 아니면 새 값을 반환한다")
    void whenNewValueIsNotNull_thenNewValueIsReturned() {
        String result = ValueUtil.defaultIfNull(
            "new",
            "existing"
        );

        assertThat(result).isEqualTo("new");
    }

    @ParameterizedTest
    @DisplayName("새 문자열이 null이거나 공백이면 기존 문자열을 반환한다")
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void whenNewStringIsNullOrBlank_thenExistingStringIsReturned(String newString) {
        String existingValue = "existing";
        String result = ValueUtil.defaultIfBlank(
            newString,
            existingValue
        );

        assertThat(result).isEqualTo(existingValue);
    }

    @Test
    @DisplayName("새 문자열이 null이거나 공백이면 기존 문자열을 반환한다")
    void whenNewStringIsNullOrBlank_thenExistingStringIsReturned() {
        String result1 = ValueUtil.defaultIfBlank(
            null,
            "existing"
        );
        String result2 = ValueUtil.defaultIfBlank(
            "   ",
            "existing"
        );

        assertThat(result1).isEqualTo("existing");
        assertThat(result2).isEqualTo("existing");
    }

    @Test
    @DisplayName("새 문자열이 공백이 아니면 새 문자열을 반환한다")
    void whenNewStringIsNotBlank_thenNewStringIsReturned() {
        String result = ValueUtil.defaultIfBlank(
            "new",
            "existing"
        );

        assertThat(result).isEqualTo("new");
    }

    @Test
    @DisplayName("새 컬렉션이 null이면 기존 컬렉션을 반환한다")
    void whenNewCollectionIsNull_thenExistingCollectionIsReturned() {
        List<String> existingList = List.of("existing");
        List<String> result = ValueUtil.defaultIfNullOrEmpty(
            null,
            existingList
        );

        assertThat(result).isEqualTo(existingList);
    }

    @Test
    @DisplayName("새 컬렉션이 비어 있으면 기존 컬렉션을 반환한다")
    void whenNewCollectionIsEmpty_thenExistingCollectionIsReturned() {
        List<String> existingList = List.of("existing");
        List<String> result = ValueUtil.defaultIfNullOrEmpty(
            Collections.emptyList(),
            existingList
        );

        assertThat(result).isEqualTo(existingList);
    }
    
    @Test
    @DisplayName("새 컬렉션이 비어 있지 않으면 새 컬렉션을 반환한다")
    void whenNewCollectionIsNotEmpty_thenNewCollectionIsReturned() {
        List<String> newList = List.of("new");
        List<String> result = ValueUtil.defaultIfNullOrEmpty(
            newList,
            List.of("existing")
        );

        assertThat(result).isEqualTo(newList);
    }
}
