package org.mentalk.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mentalk.auth.dto.JwtDto;
import org.mentalk.auth.dto.LocalLoginDto;
import org.mentalk.auth.dto.request.EmailCheckRequest;
import org.mentalk.auth.dto.request.LocalLoginRequest;
import org.mentalk.common.dto.ApiResponse;
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

    @PostMapping("/email/check")
    public ResponseEntity<ApiResponse> checkEmailInUse(
            @RequestBody @Valid EmailCheckRequest request) {
        authService.checkEmailInUse(request.email());

        return ResponseEntity.ok()
                             .body(ApiResponse.success("사용 가능한 이메일입니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> localLogin(@RequestBody @Valid LocalLoginRequest request) {
        LocalLoginDto loginDto = LocalLoginDto.of(request);

        JwtDto jwtDto = authService.localLogin(loginDto);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtDto.token());

        return ResponseEntity.ok()
                             .headers(headers)
                             .body(ApiResponse.success("로그인이 성공적으로 완료되었습니다."));
    }
}
