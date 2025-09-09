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
        assertThat(response.getId()).isEqualTo("user-id");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getFirstName()).isEqualTo("Test");
        assertThat(response.getLastName()).isEqualTo("User");
        assertThat(response.getFullName()).isEqualTo("Test User");
        assertThat(response.getRoles()).containsExactly("customer", "employee");
        assertThat(response.getStatus()).isEqualTo(ActiveStatus.ACTIVE);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
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
        assertThat(response.getId()).isEqualTo("user-id");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getFirstName()).isNull();
        assertThat(response.getLastName()).isNull();
        assertThat(response.getFullName()).isEqualTo("testuser"); // fullName은 User 도메인에서 처리
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
        assertThat(response.getRoles()).isNull();
    }
}
