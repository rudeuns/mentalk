package org.mentalk.auth;

import jakarta.validation.Valid;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mentalk.auth.dto.JwtDto;
import org.mentalk.auth.dto.LocalLoginDto;
import org.mentalk.auth.dto.request.EmailCheckRequest;
import org.mentalk.auth.dto.request.LocalLoginRequest;
import org.mentalk.common.dto.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

        ResponseCookie jwtCookie = ResponseCookie.from("access_token", jwtDto.token())
                                                 .httpOnly(true)
                                                 .path("/")
                                                 .maxAge(Duration.ofDays(1))
                                                 .build();

        Map<String, String> responseData = new HashMap<>();
        responseData.put("role", String.valueOf(jwtDto.role()));

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                             .body(ApiResponse.success("로그인이 성공적으로 완료되었습니다.", responseData));
    }
}
