package com.petry.domain.dto;

import javax.validation.constraints.NotBlank;

public record UserWithdrawDto(@NotBlank(message = "비밀번호를 입력해주세요") String checkPassword) {
}
