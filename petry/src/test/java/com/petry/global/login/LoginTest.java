package com.petry.global.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petry.domain.user.entity.User;
import com.petry.domain.user.repository.UserRepository;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired EntityManager em;

    PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    ObjectMapper objectMapper = new ObjectMapper();

    private static String KEY_ACCOUNT = "account";
    private static String KEY_PASSWORD = "password";
    private static String ACCOUNT = "account";
    private static String PASSWORD = "qwer1234";

    private static String LOGIN_RUL = "/login";

    private void clear() {
        em.flush();
        em.clear();
    }


    @BeforeEach
    private void init() {
        userRepository.save(User.builder()
                .uAccount(ACCOUNT)
                .uPassword(delegatingPasswordEncoder.encode(PASSWORD))
                .uName("user1")
                .uEmail("user1@email.coom")
                .build());
        clear();
    }

    private Map getAccountPasswordMap(String account, String password) {
        Map<String, String> map = new HashMap<>();
        map.put(KEY_ACCOUNT, account);
        map.put(KEY_PASSWORD, password);
        return map;
    }

    private ResultActions perform(String url, MediaType mediaType, Map accountPasswordMap) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(mediaType.toString())
                .content(objectMapper.writeValueAsString(accountPasswordMap)));
    }

    @Test
    public void 로그인_성공() throws Exception {
        Map<String, String> map = getAccountPasswordMap(ACCOUNT, PASSWORD);

        MvcResult result = perform(LOGIN_RUL, APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void 로그인_실패_아이디오류() throws Exception {
        Map<String, String> map = getAccountPasswordMap(ACCOUNT+"123", PASSWORD);

        MvcResult result = perform(LOGIN_RUL, APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void 로그인_실패_비밀번호오류() throws Exception {
        Map<String, String> map = getAccountPasswordMap(ACCOUNT, PASSWORD+"123");

        MvcResult result = perform(LOGIN_RUL, APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void 로그인_실패_주소오류() throws Exception {
        Map<String, String> map = getAccountPasswordMap(ACCOUNT, PASSWORD);

        perform(LOGIN_RUL+"123", APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void 로그인_데이터형식오류() throws Exception {
        Map<String, String> map = getAccountPasswordMap(ACCOUNT, PASSWORD);

        MvcResult result = perform(LOGIN_RUL, APPLICATION_FORM_URLENCODED, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void 로그인_METHOD_GET () throws Exception {
        Map<String, String> map = getAccountPasswordMap(ACCOUNT, PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders
                    .get(LOGIN_RUL)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void 로그인_METHOD_PUT () throws Exception {
        Map<String, String> map = getAccountPasswordMap(ACCOUNT, PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(LOGIN_RUL)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}
