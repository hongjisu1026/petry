package com.petry.domain.service;

import com.petry.domain.dto.UserInfoDto;
import com.petry.domain.dto.UserSignUpDto;

public interface UserService {

    void signUp(UserSignUpDto userSignUpDto) throws Exception;
    void updatePassword(String checkPassword, String toBePassword) throws Exception;
    void withdraw(String checkPassword) throws Exception;

    UserInfoDto getInfo(Long id) throws Exception;
    UserInfoDto getMyInfo() throws Exception;
}
