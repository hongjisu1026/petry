package com.petry.global.jwt.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.HandshakeRequest;
import java.io.IOException;

public interface JwtService {

    String createAccessToken(String account);
    String createRefreshToken();

    void updateRefreshToken(String account, String refreshToken);
    void destroyRefreshToken(String account);
    void sendToken(HttpServletResponse response, String accessToken, String refreshToken) throws IOException;

    String extractAccessToken(HttpServletRequest request) throws IOException, ServletException;
    String extractRefreshToken(HttpServletRequest request) throws IOException, ServletException;
    String extractAccount(String accessToken);


    void setAccessTokenHeader(HttpServletResponse response, String accessToken);

    void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);


}
