package com.petry.domain.user.service;

import com.petry.domain.user.dto.UserInfoDto;
import com.petry.domain.user.dto.UserSignUpDto;
import com.petry.domain.user.entity.User;
import com.petry.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signUp(UserSignUpDto userSignUpDto) throws Exception {
        User user = userSignUpDto.toEntity();
        user.encodePassword(passwordEncoder);

        if(userRepository.findByuAccount(userSignUpDto.account()).isPresent()) {
            throw new Exception("이미 존재하는 아이디입니다.");
        }
        userRepository.save(user);
    }

    @Override
    public void updatePassword(String checkPassword, String toBePassword) throws Exception {
        User user = userRepository.findByuAccount(SecurityUtil.getLoginAccount()).orElseThrow(() -> new Exception("존재하지 않는 회원입니다."));

        if(!user.matchPassword(passwordEncoder, checkPassword)) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        user.updatePassword(passwordEncoder, toBePassword);
    }

    @Override
    public void withdraw(String checkPassword) throws Exception {
        User user = userRepository.findByuAccount(SecurityUtil.getLoginAccount()).orElseThrow(() -> new Exception("존재하지 않는 회원입니다."));

        if(!user.matchPassword(passwordEncoder, checkPassword)) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        userRepository.delete(user);
    }

    @Override
    public UserInfoDto getInfo(Long id) throws Exception {
        User findUser = userRepository.findById(id).orElseThrow(() -> new Exception("존재하지 않는 회원입니다."));
        return new UserInfoDto(findUser);
    }

    @Override
    public UserInfoDto getMyInfo() throws Exception {
        User findUser = userRepository.findByuAccount(SecurityUtil.getLoginAccount()).orElseThrow(() -> new Exception("존재하지 않는 회원입니다."));
        return new UserInfoDto(findUser);
    }




}
