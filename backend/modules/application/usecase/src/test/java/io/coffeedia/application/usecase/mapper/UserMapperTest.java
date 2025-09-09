package io.coffeedia.application.usecase.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import io.coffeedia.application.usecase.dto.UserResponse;
import io.coffeedia.domain.model.User;
import io.coffeedia.domain.vo.ActiveStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * UserMapper 테스트
 */
@DisplayName("User 매퍼")
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    @DisplayName("User 도메인 모델을 UserResponse DTO로 변환할 수 있다")
    void toResponse() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
            .id("user-id")
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .roles(List.of("customer", "employee"))
            .status(ActiveStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .build();

        // when
        UserResponse response = userMapper.toResponse(user);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("user-id");
        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.firstName()).isEqualTo("Test");
        assertThat(response.lastName()).isEqualTo("User");
        assertThat(response.fullName()).isEqualTo("Test User");
        assertThat(response.roles()).containsExactly("customer", "employee");
        assertThat(response.status()).isEqualTo(ActiveStatus.ACTIVE);
        assertThat(response.createdAt()).isEqualTo(now);
        assertThat(response.updatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("이름이 없는 User도 변환할 수 있다")
    void toResponseWithoutNames() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
            .id("user-id")
            .username("testuser")
            .email("test@example.com")
            .roles(List.of("customer"))
            .status(ActiveStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .build();

        // when
        UserResponse response = userMapper.toResponse(user);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("user-id");
        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.firstName()).isNull();
        assertThat(response.lastName()).isNull();
        assertThat(response.fullName()).isEqualTo("testuser"); // fullName은 User 도메인에서 처리
    }

    @Test
    @DisplayName("역할이 없는 User도 변환할 수 있다")
    void toResponseWithoutRoles() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
            .id("user-id")
            .username("testuser")
            .email("test@example.com")
            .status(ActiveStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .build();

        // when
        UserResponse response = userMapper.toResponse(user);

        // then
        assertThat(response).isNotNull();
        assertThat(response.roles()).isNull();
    }
}
