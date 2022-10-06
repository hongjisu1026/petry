package com.petry.global.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petry.domain.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
public class JwtServiceImpl implements JwtService{

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInSeconds;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;


    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String ACCOUNT_CLAIM = "account";
    private static final String BEARER = "Bearer";

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;


    @Override
    public String createAccessToken(String account) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds*1000))
                .withClaim(ACCOUNT_CLAIM, account)
                .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public String createRefreshToken() {
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds*1000))
                .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public void updateRefreshToken(String account, String refreshToken) {
        userRepository.findByuAccount(account)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken), () -> new Exception("회원이 존재하지 않습니다.")
                );
    }

    @Override
    public void destroyRefreshToken(String account) {
        userRepository.findByuAccount(account)
                .ifPresentOrElse(
                        user -> user.destroyRefreshToken(), () -> new Exception("회원이 존재하지 않습니다.")
                );
    }


    @Override
    public void sendToken(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
        tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);

        String token = objectMapper.writeValueAsString(tokenMap);

        response.getWriter().write(token);
    }

    @Override
    public String extractAccessToken(HttpServletRequest request) throws IOException, ServletException {
        return Optional.ofNullable(request.getHeader(accessHeader)).map(accessToken -> accessToken.replace(BEARER, "")).orElse(null);
    }

    @Override
    public String extractRefreshToken(HttpServletRequest request) throws IOException, ServletException {
        return Optional.ofNullable(request.getHeader(refreshHeader)).map(refreshToken -> refreshToken.replace(BEARER, "")).orElse(null);
    }


    @Override
    public String extractAccount(String accessToken) {
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getClaim(ACCOUNT_CLAIM).asString();
    }

    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    @Override
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }



}
