package com.petry.domain.user.service;

import com.petry.domain.user.User;
import com.petry.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        User user = userRepository.findByuAccount(account).orElseThrow(() -> new UsernameNotFoundException("아이디가 존재하지 않습니다."));

        return (UserDetails) User.builder()
                .uAccount(user.getUAccount())
                .uPassword(user.getUPassword())
                .build();
    }
}
