package com.petry.domain.user.service;

import com.petry.domain.user.dto.UserInfoDto;
import com.petry.domain.user.dto.UserSignUpDto;

public interface UserService {

    void signUp(UserSignUpDto userSignUpDto) throws Exception;
    void updatePassword(String checkPassword, String toBePassword) throws Exception;
    void withdraw(String checkPassword) throws Exception;

    UserInfoDto getInfo(Long id) throws Exception;
    UserInfoDto getMyInfo() throws Exception;
}
