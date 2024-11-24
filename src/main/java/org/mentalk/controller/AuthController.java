package org.mentalk.controller;

import lombok.RequiredArgsConstructor;
import org.mentalk.dto.request.EmailCheckRequest;
import org.mentalk.dto.request.LoginRequest;
import org.mentalk.dto.request.PhoneNumberCheckRequest;
import org.mentalk.dto.response.ApiResponse;
import org.mentalk.dto.service.EmailDto;
import org.mentalk.dto.service.JwtDto;
import org.mentalk.dto.service.LoginDto;
import org.mentalk.dto.service.PhoneNumberDto;
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
    public ResponseEntity<ApiResponse> checkEmailDuplicate(@RequestBody EmailCheckRequest request) {
        EmailDto emailDto = EmailDto.of(request);
        authService.checkEmailDuplicate(emailDto);
        return ResponseEntity.ok()
                             .body(ApiResponse.success("사용 가능한 이메일입니다."));
    }

    @PostMapping("/check-phone-number")
    public ResponseEntity<ApiResponse> checkPhoneNumberDuplicate(
            @RequestBody PhoneNumberCheckRequest request) {
        PhoneNumberDto phoneNumberDto = PhoneNumberDto.of(request);
        authService.checkPhoneNumberDuplicate(phoneNumberDto);
        return ResponseEntity.ok()
                             .body(ApiResponse.success("신규 회원입니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        LoginDto loginDto = LoginDto.of(request);
        JwtDto jwtDto = authService.login(loginDto);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtDto.token());

        return ResponseEntity.ok()
                             .headers(headers)
                             .body(ApiResponse.success("로그인이 성공적으로 완료되었습니다."));
    }
}
