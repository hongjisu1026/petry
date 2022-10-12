package com.petry.domain.controller;

import com.petry.domain.dto.UpdatePasswordDto;
import com.petry.domain.dto.UserWithdrawDto;
import com.petry.domain.dto.UserInfoDto;
import com.petry.domain.dto.UserSignUpDto;
import com.petry.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signUp")
    @ResponseStatus(HttpStatus.OK)
    public void signUp(@Valid @RequestBody UserSignUpDto userSignUpDto) throws Exception {
        userService.signUp(userSignUpDto);
    }

    //비밀번호 변경
    @PutMapping("/user/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) throws Exception {
        userService.updatePassword(updatePasswordDto.checkPassword(), updatePasswordDto.toBePassword());
    }

    //회원 탈퇴
    @DeleteMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public void withdraw(@Valid @RequestBody UserWithdrawDto userWithdrawDto) throws Exception {
        userService.withdraw(userWithdrawDto.checkPassword());
    }

    //정보 조회
    @GetMapping("/user/{id}")
    public ResponseEntity getInfo(@Valid @PathVariable("id") Long id) throws Exception {
        UserInfoDto info = userService.getInfo(id);
        return new ResponseEntity(info, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity getMyInfo(HttpServletResponse response) throws Exception {
        UserInfoDto info = userService.getMyInfo();
        return new ResponseEntity(info, HttpStatus.OK);
    }
}
