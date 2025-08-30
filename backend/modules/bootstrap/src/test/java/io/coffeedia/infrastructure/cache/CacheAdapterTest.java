package io.coffeedia.infrastructure.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import io.coffeedia.IntegrationSupportTest;
import io.coffeedia.domain.vo.RoastLevel;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CacheAdapterTest extends IntegrationSupportTest {

    @Autowired
    private CacheAdapter cacheAdapter;

    private static final String TEST_KEY_PREFIX = "test:cache-adapter:";

    @Nested
    @DisplayName("캐시 키 존재 여부 확인")
    class ExistsTest {

        @Test
        @DisplayName("캐시에 저장된 키를 조회하면 존재함을 확인할 수 있다")
        void returnsTrueWhenKeyExists() {
            // given
            String key = TEST_KEY_PREFIX + "exists-true";
            cacheAdapter.write(key, "test-value", Duration.ofMinutes(1));

            // when
            boolean result = cacheAdapter.exists(key);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("캐시에 저장되지 않은 키를 조회하면 존재하지 않음을 확인할 수 있다")
        void returnsFalseWhenKeyNotExists() {
            // given
            String key = TEST_KEY_PREFIX + "exists-false";

            // when
            boolean result = cacheAdapter.exists(key);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("문자열 데이터 저장 및 조회")
    class StringOperationTest {

        @Test
        @DisplayName("영문 문자열을 저장하면 동일한 값으로 조회된다")
        void storesAndRetrievesEnglishString() {
            // given
            String key = TEST_KEY_PREFIX + "string-test";
            String value = "Hello, CacheAdapter!";

            // when
            cacheAdapter.write(key, value, Duration.ofMinutes(1));
            String result = cacheAdapter.read(key);

            // then
            assertThat(result).isEqualTo(value);
        }

        @Test
        @DisplayName("한글 문자열을 저장하면 동일한 값으로 조회된다")
        void storesAndRetrievesKoreanString() {
            // given
            String key = TEST_KEY_PREFIX + "korean-string";
            String value = "안녕하세요, 캐시 어댑터입니다!";

            // when
            cacheAdapter.write(key, value, Duration.ofMinutes(1));
            String result = cacheAdapter.read(key);

            // then
            assertThat(result).isEqualTo(value);
        }

        @Test
        @DisplayName("빈 문자열을 저장하면 동일한 값으로 조회된다")
        void storesAndRetrievesEmptyString() {
            // given
            String key = TEST_KEY_PREFIX + "empty-string";
            String value = "";

            // when
            cacheAdapter.write(key, value, Duration.ofMinutes(1));
            String result = cacheAdapter.read(key);

            // then
            assertThat(result).isEqualTo(value);
        }
    }

    @Nested
    @DisplayName("객체 데이터 저장 및 조회")
    class ObjectOperationTest {

        @Test
        @DisplayName("열거형 객체를 저장하면 동일한 값으로 조회된다")
        void storesAndRetrievesEnum() {
            // given
            String key = TEST_KEY_PREFIX + "enum-test";
            RoastLevel roastLevel = RoastLevel.DARK;

            // when
            cacheAdapter.write(key, roastLevel, Duration.ofMinutes(1));
            RoastLevel result = cacheAdapter.read(key, RoastLevel.class);

            // then
            assertThat(result).isEqualTo(roastLevel);
        }

        @Test
        @DisplayName("리스트 객체를 저장하면 동일한 순서와 값으로 조회된다")
        void storesAndRetrievesList() {
            // given
            String key = TEST_KEY_PREFIX + "list-test";
            List<String> list = List.of("item1", "item2", "item3");

            // when
            cacheAdapter.write(key, list, Duration.ofMinutes(1));
            List<String> result = cacheAdapter.read(key, List.class);

            // then
            assertThat(result).containsExactly("item1", "item2", "item3");
        }

        @Test
        @DisplayName("맵 객체를 저장하면 동일한 키와 값으로 조회된다")
        void storesAndRetrievesMap() {
            // given
            String key = TEST_KEY_PREFIX + "map-test";
            Map<String, Object> map = Map.of(
                "name", "Ethiopian Coffee",
                "roastLevel", "MEDIUM",
                "price", 15000
            );

            // when
            cacheAdapter.write(key, map, Duration.ofMinutes(1));
            Map<String, Object> result = cacheAdapter.read(key, Map.class);

            // then
            assertThat(result).containsAllEntriesOf(map);
        }

        @Test
        @DisplayName("복합 객체를 저장하면 모든 속성이 동일한 값으로 조회된다")
        void storesAndRetrievesComplexObject() {
            // given
            String key = TEST_KEY_PREFIX + "complex-object";
            TestCoffeeData coffeeData = TestCoffeeData.builder()
                .name("Blue Mountain")
                .roastLevel(RoastLevel.MEDIUM)
                .price(25000)
                .tags(List.of("premium", "jamaica"))
                .metadata(Map.of("altitude", "1500m", "process", "washed"))
                .build();

            // when
            cacheAdapter.write(key, coffeeData, Duration.ofMinutes(1));
            TestCoffeeData result = cacheAdapter.read(key, TestCoffeeData.class);

            // then
            assertThat(result).isEqualTo(coffeeData);
        }
    }

    @Nested
    @DisplayName("캐시 만료 시간 관리")
    class TTLTest {

        @Test
        @DisplayName("만료 시간이 설정된 데이터는 시간 경과 후 캐시에서 제거된다")
        void deletesKeyAfterTTLExpires() {
            // given
            String key = TEST_KEY_PREFIX + "ttl-expire";
            String value = "expiring-value";
            Duration ttl = Duration.ofSeconds(2);

            // when
            cacheAdapter.write(key, value, ttl);

            // then
            assertThat(cacheAdapter.exists(key)).isTrue();

            await()
                .atMost(Duration.ofSeconds(3))
                .pollInterval(Duration.ofMillis(100))
                .until(() -> !cacheAdapter.exists(key));
        }
    }

    @Nested
    @DisplayName("캐시 데이터 삭제")
    class DeleteTest {

        @Test
        @DisplayName("캐시에 저장된 데이터를 삭제하면 더 이상 조회되지 않는다")
        void deletesExistingKeyFromCache() {
            // given
            String key = TEST_KEY_PREFIX + "delete-existing";
            cacheAdapter.write(key, "to-be-deleted", Duration.ofMinutes(1));
            assertThat(cacheAdapter.exists(key)).isTrue();

            // when
            cacheAdapter.delete(key);

            // then
            assertThat(cacheAdapter.exists(key)).isFalse();
            assertThat(cacheAdapter.read(key)).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 키를 삭제해도 예외가 발생하지 않는다")
        void handlesNonExistentKeyDeletionGracefully() {
            // given
            String key = TEST_KEY_PREFIX + "delete-non-existent";

            // when & then
            assertThatNoException().isThrownBy(() -> cacheAdapter.delete(key));
        }
    }

    @Nested
    @DisplayName("예외 상황 처리")
    class ExceptionTest {

        @Test
        @DisplayName("존재하지 않는 키를 조회하면 null이 반환된다")
        void returnsNullForNonExistentKey() {
            // given
            String key = TEST_KEY_PREFIX + "non-existent";

            // when
            String stringResult = cacheAdapter.read(key);
            RoastLevel objectResult = cacheAdapter.read(key, RoastLevel.class);

            // then
            assertThat(stringResult).isNull();
            assertThat(objectResult).isNull();
        }

        @Test
        @DisplayName("저장된 데이터를 잘못된 타입으로 조회하면 null이 반환된다")
        void returnsNullForWrongType() {
            // given
            String key = TEST_KEY_PREFIX + "wrong-type";
            cacheAdapter.write(key, "string-value", Duration.ofMinutes(1));

            // when
            Integer wrongTypeResult = cacheAdapter.read(key, Integer.class);

            // then
            assertThat(wrongTypeResult).isNull();
        }
    }

    @Nested
    @DisplayName("데이터 일관성 보장")
    class ConsistencyTest {

        @Test
        @DisplayName("동일한 키에 여러 번 저장하면 마지막에 저장된 값이 조회된다")
        void overwritesValueForSameKey() {
            // given
            String key = TEST_KEY_PREFIX + "overwrite";

            // when
            cacheAdapter.write(key, "first-value", Duration.ofMinutes(1));
            cacheAdapter.write(key, "second-value", Duration.ofMinutes(1));

            // then
            String result = cacheAdapter.read(key);
            assertThat(result).isEqualTo("second-value");
        }

        @Test
        @DisplayName("동일한 키에 다른 타입의 데이터를 저장하면 타입이 변경되어 저장된다")
        void changesTypeWhenStoringDifferentTypes() {
            // given
            String key = TEST_KEY_PREFIX + "type-change";

            // when
            cacheAdapter.write(key, "string-value", Duration.ofMinutes(1));
            cacheAdapter.write(key, RoastLevel.LIGHT, Duration.ofMinutes(1));

            // then
            String stringResult = cacheAdapter.read(key);
            RoastLevel enumResult = cacheAdapter.read(key, RoastLevel.class);

            assertThat(stringResult).isEqualTo("LIGHT");
            assertThat(enumResult).isEqualTo(RoastLevel.LIGHT);
        }
    }

    @Builder
    public record TestCoffeeData(
        String name,
        RoastLevel roastLevel,
        Integer price,
        List<String> tags,
        Map<String, String> metadata
    ) {

    }
}
