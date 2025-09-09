package io.coffeedia.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import io.coffeedia.domain.vo.ActiveStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * User 도메인 모델 테스트
 */
@DisplayName("User 도메인 모델")
class UserTest {

    @Nested
    @DisplayName("사용자 생성")
    class CreateUserTest {

        @Test
        @DisplayName("모든 필드를 포함한 사용자를 생성할 수 있다")
        void createUserWithAllFields() {
            // given
            LocalDateTime now = LocalDateTime.now();
            List<String> roles = List.of("customer", "employee");

            // when
            User user = User.builder()
                .id("user-id")
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .roles(roles)
                .status(ActiveStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

            // then
            assertThat(user.getId()).isEqualTo("user-id");
            assertThat(user.getUsername()).isEqualTo("testuser");
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getFirstName()).isEqualTo("Test");
            assertThat(user.getLastName()).isEqualTo("User");
            assertThat(user.getRoles()).containsExactly("customer", "employee");
            assertThat(user.getStatus()).isEqualTo(ActiveStatus.ACTIVE);
            assertThat(user.getCreatedAt()).isEqualTo(now);
            assertThat(user.getUpdatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("최소 필드만으로 사용자를 생성할 수 있다")
        void createUserWithMinimumFields() {
            // when
            User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .status(ActiveStatus.ACTIVE)
                .build();

            // then
            assertThat(user.getUsername()).isEqualTo("testuser");
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getStatus()).isEqualTo(ActiveStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("전체 이름 생성")
    class GetFullNameTest {

        @Test
        @DisplayName("이름과 성이 모두 있으면 전체 이름을 반환한다")
        void getFullNameWithFirstAndLastName() {
            // given
            User user = User.builder()
                .username("testuser")
                .firstName("John")
                .lastName("Doe")
                .build();

            // when
            String fullName = user.getFullName();

            // then
            assertThat(fullName).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("이름만 있으면 이름만 반환한다")
        void getFullNameWithFirstNameOnly() {
            // given
            User user = User.builder()
                .username("testuser")
                .firstName("John")
                .build();

            // when
            String fullName = user.getFullName();

            // then
            assertThat(fullName).isEqualTo("John");
        }

        @Test
        @DisplayName("성만 있으면 성만 반환한다")
        void getFullNameWithLastNameOnly() {
            // given
            User user = User.builder()
                .username("testuser")
                .lastName("Doe")
                .build();

            // when
            String fullName = user.getFullName();

            // then
            assertThat(fullName).isEqualTo("Doe");
        }

        @Test
        @DisplayName("이름과 성이 모두 없으면 사용자명을 반환한다")
        void getFullNameWithoutNames() {
            // given
            User user = User.builder()
                .username("testuser")
                .build();

            // when
            String fullName = user.getFullName();

            // then
            assertThat(fullName).isEqualTo("testuser");
        }
    }

    @Nested
    @DisplayName("활성 상태 확인")
    class IsActiveTest {

        @Test
        @DisplayName("ACTIVE 상태인 사용자는 활성 상태이다")
        void activeUserIsActive() {
            // given
            User user = User.builder()
                .username("testuser")
                .status(ActiveStatus.ACTIVE)
                .build();

            // when & then
            assertThat(user.isActive()).isTrue();
        }

        @Test
        @DisplayName("INACTIVE 상태인 사용자는 비활성 상태이다")
        void inactiveUserIsNotActive() {
            // given
            User user = User.builder()
                .username("testuser")
                .status(ActiveStatus.INACTIVE)
                .build();

            // when & then
            assertThat(user.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("역할 확인")
    class RoleTest {

        @Test
        @DisplayName("특정 역할을 가지고 있는지 확인할 수 있다")
        void hasSpecificRole() {
            // given
            User user = User.builder()
                .username("testuser")
                .roles(List.of("customer", "employee"))
                .build();

            // when & then
            assertThat(user.hasRole("customer")).isTrue();
            assertThat(user.hasRole("employee")).isTrue();
            assertThat(user.hasRole("admin")).isFalse();
        }

        @Test
        @DisplayName("역할이 없는 사용자는 어떤 역할도 가지지 않는다")
        void userWithoutRoles() {
            // given
            User user = User.builder()
                .username("testuser")
                .build();

            // when & then
            assertThat(user.hasRole("customer")).isFalse();
            assertThat(user.hasRole("employee")).isFalse();
        }

        @Test
        @DisplayName("고객 권한을 확인할 수 있다")
        void isCustomer() {
            // given
            User customerUser = User.builder()
                .username("customer")
                .roles(List.of("customer"))
                .build();

            User nonCustomerUser = User.builder()
                .username("employee")
                .roles(List.of("employee"))
                .build();

            // when & then
            assertThat(customerUser.isCustomer()).isTrue();
            assertThat(nonCustomerUser.isCustomer()).isFalse();
        }

        @Test
        @DisplayName("직원 권한을 확인할 수 있다")
        void isEmployee() {
            // given
            User employeeUser = User.builder()
                .username("employee")
                .roles(List.of("employee"))
                .build();

            User nonEmployeeUser = User.builder()
                .username("customer")
                .roles(List.of("customer"))
                .build();

            // when & then
            assertThat(employeeUser.isEmployee()).isTrue();
            assertThat(nonEmployeeUser.isEmployee()).isFalse();
        }
    }
}
