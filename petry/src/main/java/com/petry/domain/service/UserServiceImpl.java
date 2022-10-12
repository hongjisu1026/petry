package com.petry.domain.service;

import com.petry.domain.entity.User;
import com.petry.domain.dto.UserInfoDto;
import com.petry.domain.dto.UserSignUpDto;
import com.petry.domain.exception.UserException;
import com.petry.domain.exception.UserExceptionType;
import com.petry.domain.repository.UserRepository;
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
            throw new UserException(UserExceptionType.ALREADY_EXIST_ACCOUNT);
        }
        userRepository.save(user);
    }

    @Override
    public void updatePassword(String checkPassword, String toBePassword) throws Exception {
        User user = userRepository.findByuAccount(SecurityUtil.getLoginAccount()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));

        if(!user.matchPassword(passwordEncoder, checkPassword)) {
            throw new UserException(UserExceptionType.WRONG_PASSWORD);
        }

        user.updatePassword(passwordEncoder, toBePassword);
    }

    @Override
    public void withdraw(String checkPassword) throws Exception {
        User user = userRepository.findByuAccount(SecurityUtil.getLoginAccount()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));

        if(!user.matchPassword(passwordEncoder, checkPassword)) {
            throw new UserException(UserExceptionType.WRONG_PASSWORD);
        }

        userRepository.delete(user);
    }

    @Override
    public UserInfoDto getInfo(Long id) throws Exception {
        User findUser = userRepository.findById(id).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        return new UserInfoDto(findUser);
    }

    @Override
    public UserInfoDto getMyInfo() throws Exception {
        User findUser = userRepository.findByuAccount(SecurityUtil.getLoginAccount()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        return new UserInfoDto(findUser);
    }




}
