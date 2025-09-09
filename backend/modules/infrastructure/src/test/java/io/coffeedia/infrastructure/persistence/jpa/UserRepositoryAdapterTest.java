package io.coffeedia.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import io.coffeedia.domain.model.User;
import io.coffeedia.domain.vo.ActiveStatus;
import io.coffeedia.infrastructure.persistence.jpa.entity.UserJpaEntity;
import io.coffeedia.infrastructure.persistence.jpa.mapper.UserJpaMapper;
import io.coffeedia.infrastructure.persistence.jpa.repository.UserJpaRepository;
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
 * UserRepositoryAdapter 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Repository 어댑터")
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserJpaMapper userJpaMapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private User sampleUser;
    private UserJpaEntity sampleUserEntity;

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

        sampleUserEntity = UserJpaEntity.builder()
            .id("user-id")
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .roles(List.of("customer"))
            .status(ActiveStatus.ACTIVE)
            .build();
    }

    @Nested
    @DisplayName("사용자명으로 조회")
    class FindByUsernameTest {

        @Test
        @DisplayName("존재하는 사용자명으로 조회하면 사용자를 반환한다")
        void findByExistingUsername() {
            // given
            String username = "testuser";
            given(userJpaRepository.findByUsername(username)).willReturn(
                Optional.of(sampleUserEntity));
            given(userJpaMapper.toDomain(sampleUserEntity)).willReturn(sampleUser);

            // when
            Optional<User> result = userRepositoryAdapter.findByUsername(username);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo(username);

            verify(userJpaRepository).findByUsername(username);
            verify(userJpaMapper).toDomain(sampleUserEntity);
        }

        @Test
        @DisplayName("존재하지 않는 사용자명으로 조회하면 빈 Optional을 반환한다")
        void findByNonExistingUsername() {
            // given
            String username = "nonexistent";
            given(userJpaRepository.findByUsername(username)).willReturn(Optional.empty());

            // when
            Optional<User> result = userRepositoryAdapter.findByUsername(username);

            // then
            assertThat(result).isEmpty();

            verify(userJpaRepository).findByUsername(username);
        }
    }

    @Nested
    @DisplayName("이메일로 조회")
    class FindByEmailTest {

        @Test
        @DisplayName("존재하는 이메일로 조회하면 사용자를 반환한다")
        void findByExistingEmail() {
            // given
            String email = "test@example.com";
            given(userJpaRepository.findByEmail(email)).willReturn(Optional.of(sampleUserEntity));
            given(userJpaMapper.toDomain(sampleUserEntity)).willReturn(sampleUser);

            // when
            Optional<User> result = userRepositoryAdapter.findByEmail(email);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo(email);

            verify(userJpaRepository).findByEmail(email);
            verify(userJpaMapper).toDomain(sampleUserEntity);
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 조회하면 빈 Optional을 반환한다")
        void findByNonExistingEmail() {
            // given
            String email = "nonexistent@example.com";
            given(userJpaRepository.findByEmail(email)).willReturn(Optional.empty());

            // when
            Optional<User> result = userRepositoryAdapter.findByEmail(email);

            // then
            assertThat(result).isEmpty();

            verify(userJpaRepository).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("ID로 조회")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회하면 사용자를 반환한다")
        void findByExistingId() {
            // given
            String id = "user-id";
            given(userJpaRepository.findById(id)).willReturn(Optional.of(sampleUserEntity));
            given(userJpaMapper.toDomain(sampleUserEntity)).willReturn(sampleUser);

            // when
            Optional<User> result = userRepositoryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);

            verify(userJpaRepository).findById(id);
            verify(userJpaMapper).toDomain(sampleUserEntity);
        }
    }

    @Nested
    @DisplayName("사용자 저장")
    class SaveTest {

        @Test
        @DisplayName("새로운 사용자를 저장할 수 있다")
        void saveNewUser() {
            // given
            User newUser = User.builder()
                .username("newuser")
                .email("new@example.com")
                .status(ActiveStatus.ACTIVE)
                .build();

            UserJpaEntity newUserEntity = UserJpaEntity.builder()
                .username("newuser")
                .email("new@example.com")
                .status(ActiveStatus.ACTIVE)
                .build();

            UserJpaEntity savedEntity = UserJpaEntity.builder()
                .id("saved-id")
                .username("newuser")
                .email("new@example.com")
                .status(ActiveStatus.ACTIVE)
                .build();

            User savedUser = User.builder()
                .id("saved-id")
                .username("newuser")
                .email("new@example.com")
                .status(ActiveStatus.ACTIVE)
                .build();

            given(userJpaMapper.toEntity(newUser)).willReturn(newUserEntity);
            given(userJpaRepository.save(newUserEntity)).willReturn(savedEntity);
            given(userJpaMapper.toDomain(savedEntity)).willReturn(savedUser);

            // when
            User result = userRepositoryAdapter.save(newUser);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("saved-id");
            assertThat(result.getUsername()).isEqualTo("newuser");

            verify(userJpaMapper).toEntity(newUser);
            verify(userJpaRepository).save(newUserEntity);
            verify(userJpaMapper).toDomain(savedEntity);
        }

        @Test
        @DisplayName("기존 사용자를 업데이트할 수 있다")
        void updateExistingUser() {
            // given
            User existingUser = User.builder()
                .id("user-id")
                .username("testuser")
                .email("updated@example.com")
                .status(ActiveStatus.ACTIVE)
                .build();

            given(userJpaRepository.findById("user-id")).willReturn(Optional.of(sampleUserEntity));
            given(userJpaRepository.save(any(UserJpaEntity.class))).willReturn(sampleUserEntity);
            given(userJpaMapper.toDomain(sampleUserEntity)).willReturn(existingUser);

            // when
            User result = userRepositoryAdapter.save(existingUser);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("user-id");

            verify(userJpaRepository).findById("user-id");
            verify(userJpaMapper).updateEntity(sampleUserEntity, existingUser);
            verify(userJpaRepository).save(sampleUserEntity);
            verify(userJpaMapper).toDomain(sampleUserEntity);
        }
    }

    @Nested
    @DisplayName("존재 여부 확인")
    class ExistsTest {

        @Test
        @DisplayName("사용자명 존재 여부를 확인할 수 있다")
        void existsByUsername() {
            // given
            String username = "testuser";
            given(userJpaRepository.existsByUsername(username)).willReturn(true);

            // when
            boolean result = userRepositoryAdapter.existsByUsername(username);

            // then
            assertThat(result).isTrue();

            verify(userJpaRepository).existsByUsername(username);
        }

        @Test
        @DisplayName("이메일 존재 여부를 확인할 수 있다")
        void existsByEmail() {
            // given
            String email = "test@example.com";
            given(userJpaRepository.existsByEmail(email)).willReturn(true);

            // when
            boolean result = userRepositoryAdapter.existsByEmail(email);

            // then
            assertThat(result).isTrue();

            verify(userJpaRepository).existsByEmail(email);
        }
    }
}
