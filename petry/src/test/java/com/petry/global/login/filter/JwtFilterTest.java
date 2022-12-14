package com.petry.global.login.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petry.domain.user.User;
import com.petry.domain.user.repository.UserRepository;
import com.petry.global.jwt.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JwtFilterTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired EntityManager em;
    @Autowired JwtService jwtService;

    PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static String KEY_ACCOUNT = "account";
    private static String KEY_PASSWORD = "password";
    private static String ACCOUNT = "account";
    private static String PASSWORD = "qwer1234";

    private static String LOGIN_RUL = "/login";

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String BEARER = "Bearer";

    private ObjectMapper objectMapper = new ObjectMapper();

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

    private Map getAccessAndRefreshToken() throws Exception {
        Map<String, String> map = getAccountPasswordMap(ACCOUNT, PASSWORD);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post(LOGIN_RUL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(map))).andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);
        String refreshToken = result.getResponse().getHeader(refreshHeader);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(accessHeader, accessToken);
        tokenMap.put(refreshHeader, refreshToken);

        return tokenMap;
    }

    //AccessToken : ???????????? ??????, RefreshToken : ???????????? ??????
    @Test
    public void Access_Refresh_X() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL+"123")).andExpect(status().isForbidden());
    }

    //AccessToken : ??????, RefreshToken : ???????????? ??????
    @Test
    public void AccessToken_O() throws Exception {
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);

        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL+"123").header(accessHeader, BEARER+accessToken)).andExpectAll(status().isNotFound());
    }

    //AccessToken : ???????????? ??????, RefreshToken : ???????????? ??????
    @Test
    public void ??????????????????_AccessToken_403() throws Exception {
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);

        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL+"123").header(accessHeader, accessToken+"1"))
                .andExpectAll(status().isForbidden());
    }

    //AccessToken : ???????????? ??????, RefreshToken : ?????? -> AccessToken ?????????
    @Test
    public void RefreshToken_O() throws Exception {
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL+"123").header(refreshHeader,BEARER+refreshToken)).andExpect(status().isOk()).andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getSubject();
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    //AccessToken : ???????????? ??????, RefreshToken : ???????????? ??????
    @Test
    public void ??????????????????_RefreshToken_403() throws Exception {
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL+"123").header(refreshHeader, refreshToken)).andExpect(status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL+"123").header(refreshHeader, BEARER+refreshToken+"1")).andExpect(status().isForbidden());
    }

    //AccessToken : ??????, RefreshToken : ?????? -> AccessToken ?????????
    @Test
    public void Access_Refresh_O() throws Exception {
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL+"123")
                        .header(refreshHeader, BEARER+refreshToken)
                        .header(accessHeader, BEARER+accessToken))
                .andExpect(status().isOk()).andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken).getSubject();

        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT); //accessToken ?????????
        assertThat(responseRefreshToken).isNull(); //refreshToken??? ????????? X
    }

    //AccessToken : ???????????? ??????, RefreshToken : ?????? -> AccessToken ?????????
    @Test
    public void ??????????????????_AccessToken_?????????_RefreshToken() throws Exception {
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL+"123")
                    .header(refreshHeader, BEARER+refreshToken)
                    .header(accessToken, BEARER+accessToken+1))
                .andExpect(status().isOk()).andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken).getSubject();

        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
        assertThat(responseRefreshToken).isNull(); //refreshToken??? ????????? X
    }

    //AccessToken : ??????, RefreshToken : ???????????? ?????? -> ????????? ????????? ????????? X
    @Test
    public void ?????????_AccessToken_??????????????????_RefreshToken() throws Exception {
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL+"123")
                .header(refreshHeader, BEARER+refreshToken+1)
                .header(accessHeader, BEARER+accessToken))
                .andExpect(status().isNotFound()).andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        assertThat(responseAccessToken).isNull(); //accessToken??? ????????? X
        assertThat(responseRefreshToken).isNull(); //refreshToken??? ????????? X
    }

    //AccessToken : ???????????? ??????, RefreshToken : ???????????? ??????
    @Test
    public void ??????????????????_Access_Refresh_403() throws Exception {
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_RUL+"123")
                        .header(refreshHeader, BEARER+refreshToken+1)
                        .header(accessHeader, BEARER+accessToken+1))
                .andExpect(status().isForbidden()).andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        assertThat(responseAccessToken).isNull(); //accessToken??? ????????? X
        assertThat(responseRefreshToken).isNull(); //refreshToken??? ????????? X
    }

    @Test
    public void ??????????????????_?????????_??????X() throws Exception {
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(LOGIN_RUL)
                .header(refreshHeader, BEARER+refreshToken)
                .header(accessHeader, BEARER+accessToken))
                .andExpect(status().isOk()).andReturn();
    }


}



