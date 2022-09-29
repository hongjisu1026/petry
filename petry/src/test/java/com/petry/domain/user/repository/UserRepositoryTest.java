package com.petry.domain.user.repository;

import com.petry.domain.user.User;
import com.petry.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.jboss.logging.NDC.clear;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired EntityManager em;

    @AfterEach
    private void after(){
        em.clear();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void 성공_회원저장() throws Exception {
        User user = User.builder().uAccount("username").uPassword("12345678").uName("name").uEmail("user@email.com").build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void 오류_아이디_없음() throws Exception {
        User user = User.builder().uPassword("12345678").uName("name").uEmail("user@email.com").build();

        assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void 오류_이름_없음() throws Exception {
        User user = User.builder().uAccount("username").uPassword("12345678").uEmail("user@email.com").build();

        assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void 오류_이메일_없음() throws Exception {
        User user = User.builder().uAccount("username").uPassword("12345678").uName("name").build();

        assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void 오류_중복아이디() throws Exception {
        User user1 = User.builder().uAccount("username").uPassword("12345678").uName("name1").uEmail("user@email.com").build();
        User user2 = User.builder().uAccount("username").uPassword("12345678").uName("name2").uEmail("user@email.com").build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void 성공_비밀번호_변경() throws Exception {
        User user = User.builder().uAccount("username").uPassword("12345678").uName("name").uEmail("user@email.com").build();
        userRepository.save(user);
        clear();

        String updatePassword = "updatePassword";

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new Exception());
        findUser.updatePassword(passwordEncoder, updatePassword);
        em.flush();

        User findUpdateUser = userRepository.findById(findUser.getId()).orElseThrow(() -> new Exception());

        assertThat(findUpdateUser).isSameAs(findUser);
        assertThat(passwordEncoder.matches(updatePassword, findUpdateUser.getUPassword())).isTrue();

    }

    @Test
    @WithMockUser(roles = "USER")
    public void 성공_회원탈퇴() throws Exception {
        User user = User.builder().uAccount("username").uPassword("12345678").uName("name").uEmail("user@email.com").build();
        userRepository.save(user);
        clear();

        userRepository.delete(user);
        clear();

        assertThrows(Exception.class, () -> userRepository.findById(user.getId()).orElseThrow(() -> new Exception()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void existByUsername_정상작동() throws Exception {
        String uAccount = "username";
        User user = User.builder().uAccount("username").uPassword("12345678").uName("name").uEmail("user@email.com").build();
        userRepository.save(user);
        clear();

        assertThat(userRepository.existsByUsername(uAccount)).isTrue();
        assertThat(userRepository.existsByUsername(uAccount+"123")).isFalse();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void findByUsername_정상작동() throws Exception {
        String uAccount = "username";
        User user = User.builder().uAccount("username").uPassword("12345678").uName("name").uEmail("user@email.com").build();
        userRepository.save(user);
        clear();

        assertThat(userRepository.findByUsername(uAccount).get().getUAccount()).isEqualTo(user.getUAccount());
        assertThat(userRepository.findByUsername(uAccount).get().getUAccount()).isEqualTo(user.getUName());
        assertThat(userRepository.findByUsername(uAccount).get().getUAccount()).isEqualTo(user.getUEmail());
        assertThrows(Exception.class, () -> userRepository.findByUsername(uAccount+"123").orElseThrow(() -> new Exception()));

    }

    @Test
    @WithMockUser(roles = "USER")
    public void 회원가입시_생성시간_등록() throws Exception {
        User user = User.builder().uAccount("username").uPassword("12345678").uName("name").uEmail("user@email.com").build();
        userRepository.save(user);
        clear();

        User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new Exception());

        assertThat(findUser.getCreatedDate()).isNotNull();
        assertThat(findUser.getLastModifiedDate()).isNotNull();
    }

}