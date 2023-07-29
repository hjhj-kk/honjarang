package com.example.honjarang.domain.user.service;

import com.example.honjarang.domain.user.dto.LoginDto;
import com.example.honjarang.domain.user.dto.UserCreateDto;
import com.example.honjarang.domain.user.entity.EmailVerification;
import com.example.honjarang.domain.user.entity.User;
import com.example.honjarang.domain.user.exception.DuplicateNicknameException;
import com.example.honjarang.domain.user.exception.EmailNotVerifiedException;
import com.example.honjarang.domain.user.exception.PasswordMismatchException;
import com.example.honjarang.domain.user.exception.UserNotFoundException;
import com.example.honjarang.domain.user.repository.EmailVerificationRepository;
import com.example.honjarang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    private final EmailVerificationRepository emailVerificationRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
       return user;
    }

    @Transactional
    public void signup(UserCreateDto userCreateDto) {
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(userCreateDto.getEmail()).orElseThrow(() -> new EmailNotVerifiedException("이메일 인증이 되지 않았습니다."));
        if (!emailVerification.getIsVerified()) {
            throw new EmailNotVerifiedException("이메일 인증이 되지 않았습니다.");
        }

        User user = User.builder()
                .email(userCreateDto.getEmail())
                .password(passwordEncoder.encode(userCreateDto.getPassword()))
                .nickname(userCreateDto.getNickname())
                .address(userCreateDto.getAddress())
                .latitude(userCreateDto.getLatitude())
                .longitude(userCreateDto.getLongitude())
                .build();
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void checkNickname(String nickname) {
        if(userRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException("중복된 닉네임입니다.");
        }
    }

    @Transactional
    public void changePassword(User user, String password, String newPassword){
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
        User loginedUser = userRepository.findByEmail(user.getEmail()).orElseThrow(()->new UserNotFoundException("존재하지 않는 사용자입니다."));
        loginedUser.changePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void changeUserInfo(User user, String nickname, String address, Double latitude, Double longitude){
        User loginedUser = userRepository.findByEmail(user.getEmail()).orElseThrow(()->new UserNotFoundException("존재하지 않는 사용자입니다."));
        loginedUser.changeUserInfo(nickname,address,latitude,longitude);
    }

    @Transactional
    public void changeUserImage(User user, String profileImage){
        User loginedUser = userRepository.findByEmail(user.getEmail()).orElseThrow(()->new UserNotFoundException("존재하지 않는 회원입니다."));
        loginedUser.changeProfileImage(profileImage);
    }
}