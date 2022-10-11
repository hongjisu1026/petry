package com.petry.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petry.domain.user.dto.UserSignUpDto;
import com.petry.domain.user.entity.User;
import com.petry.domain.user.repository.UserRepository;
import com.petry.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    EntityManager em;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    ObjectMapper objectMapper = new ObjectMapper();

    private static String SIGN_UP_URL = "/signUp";

    private String account = "account";
    private String password = "qwer1234!";
    private String name = "jisu";
    private String email = "jisu@email.com";

    private void clear() {
        em.flush();
        em.clear();
    }

    private void signUp(String signUpData) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(SIGN_UP_URL).contentType(MediaType.APPLICATION_JSON).content(signUpData)).andExpect(status().isOk());
    }

    @Value("${jwt.access.header}")
    private String accessHeader;
    private static final String BEARER = "Bearer";

    private String getAccessToken() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("account", account);
        map.put("password", password);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(map))).andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(accessHeader);
    }

    //회원가입 테스트
    @Test
    public void 회원가입_성공() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        User user = userRepository.findByuAccount(account).orElseThrow(() -> new Exception("회원이 존재하지 않습니다."));
        assertThat(user.getUName()).isEqualTo(name);
        assertThat(userRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void 회원가입_실패_필드없음() throws Exception {
        String noAccountSignUpData = objectMapper.writeValueAsString(new UserSignUpDto(null, password, name, email));
        String noPasswordSignUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, null, name, email));
        String noNameSignUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, null, email));
        String noEmailSignUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, null));

        signUp(noAccountSignUpData);
        signUp(noPasswordSignUpData);
        signUp(noNameSignUpData);
        signUp(noEmailSignUpData);

        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }


    //비밀번호 변경 테스트
    @Test
    public void 비밀번호변경_성공() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);
        map.put("toBePassword", password+"!@!@!@!@");

        String updatePassword = objectMapper.writeValueAsString(map);

        mockMvc.perform(MockMvcRequestBuilders.put("/user/password")
                .header(accessHeader, BEARER+accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePassword)).andExpect(status().isOk());

        User user = userRepository.findByuAccount(account).orElseThrow(() -> new Exception("회원이 존재하지 않습니다."));
        assertThat(passwordEncoder.matches(password, user.getUPassword())).isFalse();
        assertThat(passwordEncoder.matches(password+"!@!@!@!@", user.getUPassword())).isTrue();
    }

    @Test
    public void 비밀번호변경_실패_비밀번호_불일치() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);
        map.put("toBePassword", password+"!@!@!@!@");

        String updatePassword = objectMapper.writeValueAsString(map);

        mockMvc.perform(MockMvcRequestBuilders.put("/user/password")
                .header(accessHeader, BEARER+accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePassword)).andExpect(status().isOk());

        User user = userRepository.findByuAccount(account).orElseThrow(() -> new Exception("회원이 존재하지 않습니다."));
        assertThat(passwordEncoder.matches(password, user.getUPassword())).isTrue();
        assertThat(passwordEncoder.matches(password+"!@!@!@!@", user.getUPassword())).isFalse();
    }

    @Test
    public void 비밀번호변경_실패_형식오류() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);
        map.put("toBePassword", password+"1212");

        String updatePassword = objectMapper.writeValueAsString(map);

        mockMvc.perform(MockMvcRequestBuilders.put("/user/password")
                .header(accessHeader, BEARER+accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePassword)).andExpect(status().isOk());

        User user = userRepository.findByuAccount(account).orElseThrow(() -> new Exception("회원이 존재하지 않습니다."));
        assertThat(passwordEncoder.matches(password, user.getUPassword())).isTrue();
        assertThat(passwordEncoder.matches(password+"1212", user.getUPassword())).isFalse();
    }


    //회원탈퇴 테스트
    @Test
    public void 회원탈퇴_성공() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);

        String updatePassword = objectMapper.writeValueAsString(map);

        mockMvc.perform(MockMvcRequestBuilders.delete("/user")
                .header(accessHeader, BEARER+accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePassword)).andExpect(status().isOk());

        assertThrows(Exception.class, () -> userRepository.findByuAccount(account).orElseThrow(() -> new Exception("회원이 존재하지 않습니다.")));
    }

    @Test
    public void 회원탈퇴_실패_비밀번호_불일치() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password+11);

        String updatePassword = objectMapper.writeValueAsString(map);

        mockMvc.perform(MockMvcRequestBuilders.delete("/user")
                .header(accessHeader, BEARER+accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePassword)).andExpect(status().isOk());

        User user = userRepository.findByuAccount(account).orElseThrow(() -> new Exception("회원이 존재하지 않습니다."));
        assertThat(user).isNotNull();
    }

    @Test
    public void 회원탈퇴_실패_권한없음() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", password);

        String updatePassword = objectMapper.writeValueAsString(map);

        mockMvc.perform(MockMvcRequestBuilders.delete("/user")
                .header(accessHeader, BEARER+accessToken+"1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePassword)).andExpect(status().isForbidden());

        User user = userRepository.findByuAccount(account).orElseThrow(() -> new Exception("회원이 존재하지 않습니다."));
        assertThat(user).isNotNull();
    }



    //정보조회 테스트
    @Test
    public void 내정보조회_성공() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user")
                .characterEncoding(StandardCharsets.UTF_8)
                .header(accessHeader, BEARER+accessToken))
                .andExpect(status().isOk()).andReturn();

        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        User user = userRepository.findByuAccount(account).orElseThrow(() -> new Exception("회원이 존재하지 않습니다."));
        assertThat(user.getUAccount());
        assertThat(user.getUName());
        assertThat(user.getUEmail());
    }

    @Test
    public void 내정보조회_실패_JWT없음() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER+accessToken+1))
                .andExpect(status().isForbidden());
    }

    @Test
    public void 회원정보조회_성공() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Long id = userRepository.findAll().get(0).getId();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/"+id)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(accessHeader, BEARER+accessToken))
                .andExpect(status().isOk()).andReturn();

        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        User user = userRepository.findByuAccount(account).orElseThrow(() -> new Exception("회원이 존재하지 않습니다."));
        assertThat(user.getUAccount()).isEqualTo(map.get(account));
        assertThat(user.getUName()).isEqualTo(map.get(name));
        assertThat(user.getUEmail()).isEqualTo(map.get(email));
    }

    @Test
    public void 회원정보조회_실페_없는회원() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user/2123")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER+accessToken))
                .andExpect(status().isOk()).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("");
    }

    @Test
    public void 회원정보조회_실패_JWT없음() throws Exception {
        String signUpData = objectMapper.writeValueAsString(new UserSignUpDto(account, password, name, email));
        signUp(signUpData);

        String accessToken = getAccessToken();

        mockMvc.perform(MockMvcRequestBuilders.get("/user/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(accessHeader, BEARER+accessToken))
                .andExpect(status().isForbidden());
    }
}