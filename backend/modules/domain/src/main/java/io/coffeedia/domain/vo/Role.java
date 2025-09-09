package io.coffeedia.domain.vo;

/**
 * 사용자 역할 열거형
 */
public enum Role {
    
    CUSTOMER("customer", "고객"),
    EMPLOYEE("employee", "직원"),
    ADMIN("admin", "관리자");

    private final String code;
    private final String description;

    Role(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 코드로 역할 찾기
     */
    public static Role fromCode(String code) {
        for (Role role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("잘못된 역할 코드: " + code);
    }

    /**
     * Spring Security 역할 형태로 변환 (ROLE_ 접두사 추가)
     */
    public String toSpringSecurityRole() {
        return "ROLE_" + code.toUpperCase();
    }
}
