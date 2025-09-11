package io.coffeedia.infrastructure.persistence.jpa.entity;

import io.coffeedia.domain.vo.ActiveStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 JPA 엔티티
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserJpaEntity extends BaseEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password; // 암호화된 비밀번호

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "id")
    )
    @Column(name = "role")
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ActiveStatus status = ActiveStatus.ACTIVE;

    /**
     * 역할 추가
     */
    public void addRole(String role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    /**
     * 역할 제거
     */
    public void removeRole(String role) {
        if (roles != null) {
            roles.remove(role);
        }
    }

    /**
     * 사용자 정보 업데이트
     */
    public void updateInfo(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * 비밀번호 업데이트
     */
    public void updatePassword(String password) {
        this.password = password;
    }

    /**
     * 상태 변경
     */
    public void changeStatus(ActiveStatus status) {
        this.status = status;
    }
}
