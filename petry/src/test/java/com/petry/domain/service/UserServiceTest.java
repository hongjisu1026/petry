package com.petry.domain.service;

import com.petry.domain.dto.UserInfoDto;
import com.petry.domain.dto.UserSignUpDto;
import com.petry.domain.entity.User;
import com.petry.domain.exception.UserException;
import com.petry.domain.exception.UserExceptionType;
import com.petry.domain.repository.UserRepository;
import com.petry.domain.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    String PASSWORD = "password";

    private void clear() {
        em.flush();
        em.clear();
    }

    private UserSignUpDto makeUserSignUpDto() {
        return new UserSignUpDto("account", PASSWORD, "user", "user@email.com");
    }

    private UserSignUpDto setUser() throws Exception {
        UserSignUpDto userSignUpDto = makeUserSignUpDto();
        userService.signUp(userSignUpDto);
        clear();
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .uAccount(userSignUpDto.account())
                .uPassword(userSignUpDto.password())
                .uName(userSignUpDto.name())
                .uEmail(userSignUpDto.email())
                .build(), null, null));

        SecurityContextHolder.setContext(emptyContext);
        return userSignUpDto;
    }

    @AfterEach
    public void removeUser() {
        SecurityContextHolder.createEmptyContext().setAuthentication(null);
    }

    //회원가입 테스트
    @Test
    public void 회원가입_성공() throws Exception {
        UserSignUpDto userSignUpDto = makeUserSignUpDto();

        userService.signUp(userSignUpDto);
        clear();

        User user = userRepository.findByuAccount(userSignUpDto.account()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        assertThat(user.getId()).isNotNull();
        assertThat(user.getUAccount()).isEqualTo(userSignUpDto.account());
        assertThat(user.getUName()).isEqualTo(userSignUpDto.name());
        assertThat(user.getUEmail()).isEqualTo(userSignUpDto.email());
    }

    @Test
    public void 회원가입_실패_중복아이디() throws Exception {
        UserSignUpDto userSignUpDto = makeUserSignUpDto();
        userService.signUp(userSignUpDto);
        clear();

        assertThat(assertThrows(UserException.class, () -> userService.signUp(userSignUpDto)).getExceptionType()).isEqualTo(UserExceptionType.ALREADY_EXIST_ACCOUNT);
    }

    @Test
    public void 회원가입_실패_입력하지않은_필드() throws Exception {
        UserSignUpDto userSignUpDto1 = new UserSignUpDto(null, passwordEncoder.encode(PASSWORD), "user", "user@email.com");
        UserSignUpDto userSignUpDto2 = new UserSignUpDto("account", null, "user", "user@email.com");
        UserSignUpDto userSignUpDto3 = new UserSignUpDto("account", passwordEncoder.encode(PASSWORD), null, "user@email.com");
        UserSignUpDto userSignUpDto4 = new UserSignUpDto("account", passwordEncoder.encode(PASSWORD), "user", null);

        assertThrows(Exception.class, () -> userService.signUp(userSignUpDto1));
        assertThrows(Exception.class, () -> userService.signUp(userSignUpDto2));
        assertThrows(Exception.class, () -> userService.signUp(userSignUpDto3));
        assertThrows(Exception.class, () -> userService.signUp(userSignUpDto4));

    }

    //비밀번호 변경
    @Test
    public void 비밀번호변경_성공() throws Exception {
        UserSignUpDto userSignUpDto = setUser();

        String toBePassword = "qwerasdf1234";
        userService.updatePassword(PASSWORD, toBePassword);
        clear();

        User findUser = userRepository.findByuAccount(userSignUpDto.account()).orElseThrow(() -> new Exception());
        assertThat(findUser.matchPassword(passwordEncoder, toBePassword)).isTrue();
    }

    //회원 탈퇴
    @Test
    public void 회원탈퇴() throws Exception {
        UserSignUpDto userSignUpDto = setUser();

        userService.withdraw(PASSWORD);

        assertThat(assertThrows(Exception.class, () -> userRepository.findByuAccount(userSignUpDto.account()).orElseThrow(() -> new Exception("회원이 존재하지 않습니다."))).getMessage()).isEqualTo("회원이 존재하지 않습니다.");
    }

    @Test
    public void 회원탈퇴_실패_비밀번호불일치() throws Exception {
        UserSignUpDto userSignUpDto = setUser();

        assertThat(assertThrows(UserException.class, () -> userService.withdraw(PASSWORD+"1")).getExceptionType()).isEqualTo(UserExceptionType.WRONG_PASSWORD);
    }

    @Test
    public void 회원정보조회() throws Exception {
        UserSignUpDto userSignUpDto = setUser();
        User user = userRepository.findByuAccount(userSignUpDto.account()).orElseThrow(() -> new Exception());

        UserInfoDto info = userService.getInfo(user.getId());

        assertThat(info.getAccount()).isEqualTo(userSignUpDto.account());
        assertThat(info.getName()).isEqualTo(userSignUpDto.name());
        assertThat(info.getEmail()).isEqualTo(userSignUpDto.email());
    }

    @Test
    public void 내정보조회() throws Exception {
        UserSignUpDto userSignUpDto = setUser();

        UserInfoDto myInfo = userService.getMyInfo();

        assertThat(myInfo.getAccount()).isEqualTo(userSignUpDto.account());
        assertThat(myInfo.getName()).isEqualTo(userSignUpDto.name());
        assertThat(myInfo.getEmail()).isEqualTo(userSignUpDto.email());
    }
}