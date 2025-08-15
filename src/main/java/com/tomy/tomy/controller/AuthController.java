package com.tomy.tomy.controller;

import com.tomy.tomy.dto.*;
import com.tomy.tomy.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            authService.signup(request.getUserId(), request.getPassword(), request.getBirthday(), request.getNickname(), request.getGender(), request.getAllowNotification());
            return ResponseEntity.ok(new AuthResponse("회원가입 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String accessToken = authService.login(request.getUserId(), request.getPassword());
            // In a real scenario, you'd fetch the nickname from the User object after successful login
            // For now, using a placeholder
            return ResponseEntity.ok(new LoginResponse(accessToken, "홍길동"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // For JWT, logout typically involves client-side token invalidation or server-side blacklist
        // This is a placeholder for API spec compliance
        return ResponseEntity.ok(new AuthResponse("로그아웃 완료"));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody WithdrawRequest request) {
        // In a real application, you'd get the userId from the authenticated user's context (e.g., JWT)
        // For now, assuming userId is available or passed implicitly
        try {
            authService.withdraw("current_user_id"); // Placeholder for actual user ID
            return ResponseEntity.ok(new AuthResponse("회원 탈퇴 완료"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/info/update")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        try {
            if (!request.getPassword().equals(request.getPass_check())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("비밀번호와 비밀번호 확인이 일치하지 않습니다."));
            }
            authService.updatePassword(request.getUserId(), request.getPassword());
            return ResponseEntity.ok(new AuthResponse("비밀번호 변경 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }
}
