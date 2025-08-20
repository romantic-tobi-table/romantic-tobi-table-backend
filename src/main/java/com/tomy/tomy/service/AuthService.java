package com.tomy.tomy.service;

import com.tomy.tomy.domain.User;
import com.tomy.tomy.domain.UserWithdrawal;
import com.tomy.tomy.dto.WithdrawRequest;
import com.tomy.tomy.repository.UserRepository;
import com.tomy.tomy.repository.UserWithdrawalRepository;
import com.tomy.tomy.security.JwtTokenProvider;
import com.tomy.tomy.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PetService petService;
    private final UserWithdrawalRepository userWithdrawalRepository;

    @Transactional
    public User signup(String userId, String password, String birthday, String nickname, String gender, Boolean allowNotification) {
        if (userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }

        User user = new User();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        user.setBirthday(java.time.LocalDate.parse(birthday));
        user.setNickname(nickname);
        user.setGender(gender);
        user.setAllowNotification(allowNotification);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        petService.createPet(savedUser);

        return savedUser;
    }

    @Transactional(readOnly = true)
    public String login(String userId, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userId, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    @Transactional(readOnly = true)
    public boolean checkUserIdDuplication(String userId) {
        return userRepository.existsByUserId(userId);
    }

    @Transactional
    public void withdraw(String userId, WithdrawRequest withdrawRequest) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserWithdrawal userWithdrawal = new UserWithdrawal();
        userWithdrawal.setUserId(user.getId());
        userWithdrawal.setReason(withdrawRequest.getReason());
        userWithdrawal.setDetail(withdrawRequest.getDetail());
        userWithdrawalRepository.save(userWithdrawal);

        userRepository.delete(user);
    }

    @Transactional
    public User updatePassword(String userId, String newPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}