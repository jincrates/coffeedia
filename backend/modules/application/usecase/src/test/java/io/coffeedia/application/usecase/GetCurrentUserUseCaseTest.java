package io.coffeedia.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import io.coffeedia.application.port.repository.UserRepositoryPort;
import io.coffeedia.application.usecase.dto.UserResponse;
import io.coffeedia.application.usecase.mapper.UserMapper;
import io.coffeedia.domain.exception.UserNotFoundException;
import io.coffeedia.domain.model.User;
import io.coffeedia.domain.vo.ActiveStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetCurrentUserUseCase 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("현재 사용자 정보 조회 UseCase")
class GetCurrentUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private GetCurrentUserUseCase getCurrentUserUseCase;

    private User sampleUser;
    private UserResponse sampleUserResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        sampleUser = User.builder()
            .id("user-id")
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .roles(List.of("customer"))
            .status(ActiveStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .build();

        sampleUserResponse = UserResponse.builder()
            .id("user-id")
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .fullName("Test User")
            .roles(List.of("customer"))
            .status(ActiveStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    @Nested
    @DisplayName("사용자명으로 조회")
    class ExecuteByUsernameTest {

        @Test
        @DisplayName("존재하는 사용자명으로 조회하면 사용자 정보를 반환한다")
        void executeWithExistingUsername() {
            // given
            String username = "testuser";
            given(userRepositoryPort.findByUsername(username)).willReturn(Optional.of(sampleUser));
            given(userMapper.toResponse(sampleUser)).willReturn(sampleUserResponse);

            // when
            UserResponse result = getCurrentUserUseCase.execute(username);

            // then
            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo(username);
            assertThat(result.email()).isEqualTo("test@example.com");
            assertThat(result.fullName()).isEqualTo("Test User");
            assertThat(result.roles()).containsExactly("customer");
            assertThat(result.status()).isEqualTo(ActiveStatus.ACTIVE);

            verify(userRepositoryPort).findByUsername(username);
            verify(userMapper).toResponse(sampleUser);
        }

        @Test
        @DisplayName("존재하지 않는 사용자명으로 조회하면 예외를 발생시킨다")
        void executeWithNonExistingUsername() {
            // given
            String username = "nonexistent";
            given(userRepositoryPort.findByUsername(username)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> getCurrentUserUseCase.execute(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다: " + username);

            verify(userRepositoryPort).findByUsername(username);
        }
    }

    @Nested
    @DisplayName("사용자 ID로 조회")
    class ExecuteByIdTest {

        @Test
        @DisplayName("존재하는 사용자 ID로 조회하면 사용자 정보를 반환한다")
        void executeByIdWithExistingUserId() {
            // given
            String userId = "user-id";
            given(userRepositoryPort.findById(userId)).willReturn(Optional.of(sampleUser));
            given(userMapper.toResponse(sampleUser)).willReturn(sampleUserResponse);

            // when
            UserResponse result = getCurrentUserUseCase.executeById(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(userId);
            assertThat(result.username()).isEqualTo("testuser");
            assertThat(result.email()).isEqualTo("test@example.com");

            verify(userRepositoryPort).findById(userId);
            verify(userMapper).toResponse(sampleUser);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 조회하면 예외를 발생시킨다")
        void executeByIdWithNonExistingUserId() {
            // given
            String userId = "nonexistent-id";
            given(userRepositoryPort.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> getCurrentUserUseCase.executeById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다: " + userId);

            verify(userRepositoryPort).findById(userId);
        }
    }
}
