package io.coffeedia.bootstrap.api.controller.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO
 */
@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 3, max = 20, message = "사용자명은 3자 이상 20자 이하여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "사용자명은 영문, 숫자, 언더스코어, 하이픈만 사용 가능합니다")
    private String username;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 하나 이상 포함해야 합니다")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String confirmPassword;

    @Size(max = 50, message = "이름은 50자 이하여야 합니다")
    private String firstName;

    @Size(max = 50, message = "성은 50자 이하여야 합니다")
    private String lastName;

    public SignUpRequest(String username, String email, String password, String confirmPassword,
        String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * 비밀번호와 비밀번호 확인이 일치하는지 검증
     */
    public boolean isPasswordMatched() {
        return password != null && password.equals(confirmPassword);
    }
}
