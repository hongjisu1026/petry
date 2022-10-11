package com.petry.domain.user.dto;

import com.petry.domain.user.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInfoDto {
    private String account;
    private String name;
    private String email;

    @Builder
    public UserInfoDto(User user) {
        this.account = user.getUAccount();
        this.name = user.getUName();
        this.email = user.getUEmail();
    }
}
