package io.coffeedia.domain.model;

import io.coffeedia.domain.vo.ActiveStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 도메인 모델
 */
@Getter
@Builder
public class User {

    private final String id;
    private final String username;
    private final String email;
    private final String password; // 암호화된 비밀번호
    private final String firstName;
    private final String lastName;
    private final List<String> roles;
    private final ActiveStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * 사용자의 전체 이름 반환
     */
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return username;
        }

        StringBuilder fullName = new StringBuilder();
        if (firstName != null) {
            fullName.append(firstName);
        }
        if (lastName != null) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(lastName);
        }

        return fullName.toString();
    }

    /**
     * 활성 상태 확인
     */
    public boolean isActive() {
        return status == ActiveStatus.ACTIVE;
    }

    /**
     * 특정 역할을 가지고 있는지 확인
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * 관리자 권한 확인
     */
    public boolean isEmployee() {
        return hasRole("employee");
    }

    /**
     * 고객 권한 확인
     */
    public boolean isCustomer() {
        return hasRole("customer");
    }

    /**
     * 비밀번호 검증 (암호화된 비밀번호와 비교) 주의: 이 메서드는 도메인 레이어에서 사용하지 말고, 인프라 레이어에서 PasswordEncoder를 사용하여 검증할 것
     */
    public boolean matchesPassword(String rawPassword,
        java.util.function.Function<String, Boolean> passwordMatcher) {
        return passwordMatcher.apply(rawPassword);
    }
}
