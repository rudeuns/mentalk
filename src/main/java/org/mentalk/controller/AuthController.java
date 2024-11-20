package org.mentalk.controller;

import lombok.RequiredArgsConstructor;
import org.mentalk.dto.ApiResponse;
import org.mentalk.dto.AuthRequest;
import org.mentalk.dto.JwtDto;
import org.mentalk.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse> validateEmailDuplicate(@RequestBody AuthRequest.Email emailDto) {
        authService.validateEmailDuplicate(emailDto);
        return ResponseEntity.ok()
                             .body(ApiResponse.success("사용 가능한 이메일입니다."));
    }

    @PostMapping("/check-phone")
    public ResponseEntity<ApiResponse> validatePhoneNumberDuplicate(
            @RequestBody AuthRequest.PhoneNumber phoneNumberDto) {
        authService.validatePhoneNumberDuplicate(phoneNumberDto);
        return ResponseEntity.ok()
                             .body(ApiResponse.success("신규 회원입니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthRequest.Login loginDto) {
        JwtDto jwtDto = authService.login(loginDto);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtDto.token());

        return ResponseEntity.ok()
                             .headers(headers)
                             .body(ApiResponse.success("로그인이 성공적으로 완료되었습니다."));
    }
}
