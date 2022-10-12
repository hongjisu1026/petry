package com.petry.global.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.petry.domain.entity.User;
import com.petry.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class JwtServiceTest {

    @Autowired JwtService jwtService;
    @Autowired UserRepository userRepository;
    @Autowired EntityManager em;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String ACCOUNT_CLAIM = "account";
    private static final String BEARER = "Bearer";

    private String account = "account";

    @BeforeEach
    public void init() {
        User user = User.builder().uAccount("account").uPassword("12345678").uName("name").uEmail("user@email.com").build();
        userRepository.save(user);
        clear();
    }

    private void clear() {
        em.flush();
        em.clear();
    }

    private DecodedJWT getVerify(String token) {
        return JWT.require(HMAC512(secret)).build().verify(token);
    }

    @Test
    public void AccessToken_발급() throws Exception {
        String accessToken = jwtService.createAccessToken(account);
        DecodedJWT verify = getVerify(accessToken);
        String subject = verify.getSubject();
        String findAccount = verify.getClaim(ACCOUNT_CLAIM).asString();

        assertThat(findAccount).isEqualTo(account);
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    @Test
    public void RefreshToken_발급() throws  Exception {
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);
        String subject = verify.getSubject();
        String account = verify.getClaim(ACCOUNT_CLAIM).asString();

        assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
        assertThat(account).isNull();
    }

    @Test
    public void RefreshToken_업데이트() throws Exception {
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(account, refreshToken);
        clear();
        Thread.sleep(3000);

        String reIssuedRefreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(account, reIssuedRefreshToken);
        clear();

        assertThrows(Exception.class, () -> userRepository.findByRefreshToken(refreshToken).get());
        assertThat(userRepository.findByRefreshToken(reIssuedRefreshToken).get().getUAccount()).isEqualTo(account);
    }

    @Test
    public void RefreshToken_제거() throws Exception {
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(account, refreshToken);
        clear();

        jwtService.destroyRefreshToken(account);
        clear();

        assertThrows(Exception.class, () -> userRepository.findByRefreshToken(refreshToken).get());

        User user = userRepository.findByuAccount(account).get();
        assertThat(user.getRefreshToken()).isNull();
    }
    
    @Test
    public void 유효성_검사() throws Exception {
        String accessToken = jwtService.createAccessToken(account);
        String refreshToken = jwtService.createRefreshToken();
        
        assertThat(jwtService.isTokenValid(accessToken)).isTrue();
        assertThat(jwtService.isTokenValid(refreshToken)).isTrue();
        assertThat(jwtService.isTokenValid(accessToken+"a")).isFalse();
        assertThat(jwtService.isTokenValid(refreshToken+"a")).isFalse();
    }

    @Test
    public void AccessToken_헤더설정() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(account);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);

        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        assertThat(headerAccessToken).isEqualTo(accessToken);
    }

    @Test
    public void RefreshToken_헤더설정() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(account);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.setRefreshTokenHeader(mockHttpServletResponse, refreshToken);

        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);
        assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    public void 토큰_전송() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(account);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);
        assertThat(headerRefreshToken).isEqualTo(refreshToken);
    }

    private HttpServletRequest setRequest(String accessToken, String refreshToken) throws IOException {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        httpServletRequest.addHeader(accessHeader, BEARER+headerAccessToken);
        httpServletRequest.addHeader(refreshHeader, BEARER+headerRefreshToken);

        return httpServletRequest;
    }

    @Test
    public void AccessToken_추출() throws Exception {
        String accessToken = jwtService.createAccessToken(account);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        String extractAccessToken = jwtService.extractAccessToken(httpServletRequest).orElseThrow(() -> new Exception("토큰이 없습니다"));

        assertThat(extractAccessToken).isEqualTo(accessToken);
        assertThat(getVerify(extractAccessToken).getClaim(ACCOUNT_CLAIM).asString()).isEqualTo(account);
    }

    @Test
    public void RefreshToken_추출() throws Exception {
        String accessToken = jwtService.createAccessToken(account);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        String extractRefreshToken = jwtService.extractRefreshToken(httpServletRequest).orElseThrow(() -> new Exception("토큰이 없습니다"));

        assertThat(extractRefreshToken).isEqualTo(refreshToken);
        assertThat(getVerify(extractRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT);
    }

    @Test
    public void Account_추출() throws Exception {
        String accessToken = jwtService.createAccessToken(account);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);

        String requestAccessToken = jwtService.extractAccessToken(httpServletRequest).orElseThrow(() -> new Exception("토큰이 없습니다"));

        String extractAccount = jwtService.extractAccount(requestAccessToken).orElseThrow(() -> new Exception("토큰이 없습니다"));

        assertThat(extractAccount).isEqualTo(account);
    }



}