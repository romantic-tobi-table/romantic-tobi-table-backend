package com.tomy.tomy.service;

import com.tomy.tomy.domain.Pet;
import com.tomy.tomy.domain.User;
import com.tomy.tomy.repository.PetRepository;
import com.tomy.tomy.repository.UserRepository;
import com.tomy.tomy.security.JwtTokenProvider;
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
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // Added
    private final JwtTokenProvider jwtTokenProvider; // Added

    @Transactional
    public User signup(String userId, String password, String birthday, String nickname, String gender, Boolean allowNotification) {
        if (userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        User user = new User();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        user.setBirthday(java.time.LocalDate.parse(birthday)); // Assuming YYYY-MM-DD format
        user.setNickname(nickname);
        user.setGender(gender);
        user.setAllowNotification(allowNotification);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Create and save a default Pet for the new user
        Pet pet = new Pet();
        pet.setUser(savedUser);
        pet.setLevel(1);
        pet.setCurrentPoint(0);
        pet.setExp(0);
        pet.setUpdatedAt(LocalDateTime.now());
        petRepository.save(pet);

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

    @Transactional
    public void withdraw(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public User updatePassword(String userId, String newPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
