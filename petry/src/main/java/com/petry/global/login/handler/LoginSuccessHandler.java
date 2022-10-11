package com.petry.global.login.handler;

import com.petry.domain.user.repository.UserRepository;
import com.petry.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException{
        String account = extractAccount(authentication);
        String accessToken = jwtService.createAccessToken(account);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        userRepository.findByuAccount(account).ifPresent(user -> user.updateRefreshToken(refreshToken));

        log.info("로그인에 성공했습니다. account : {}", account);
        log.info("AccessToken을 발급합니다. AccessToken : {}", accessToken);
        log.info("RefreshToken울 발급합니다. RefreshToken : {}", refreshToken);
    }

    private String extractAccount(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

}
