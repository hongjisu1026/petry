package com.petry.domain.user.dto;

import com.petry.domain.user.entity.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record UserSignUpDto(@NotBlank(message = "아이디를 입력해주세요.") @Size(min=7, max = 20, message = "아이디는 7~20자 내외로 입력해주세요.") String account,
                            @NotBlank(message = "비밀번호를 입력해주세요.")
                            @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.") String password,
                            @NotBlank(message = "이름을 입력해주세요") @Size(min=2, message = "사용자 이름이 너무 짧습니다.")
                            @Pattern(regexp = "^[A-Za-z가-힣]+$", message = "사용자 이름은 한글 또는 알파벳만 입력해주세요.") String name,
                            @NotBlank(message = "이메일을 입력해주세요")
                            @Email(message = "이메일 형식에 맞지않습니다.") String email) {

    public User toEntity() {
        return User.builder().uAccount(account).uPassword(password).uName(name).uEmail(email).build();
    }
}
