package org.mentalk.auth;

import jakarta.validation.Valid;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mentalk.auth.dto.EmailDto;
import org.mentalk.auth.dto.JwtDto;
import org.mentalk.auth.dto.LocalLoginDto;
import org.mentalk.auth.request.EmailCheckRequest;
import org.mentalk.auth.request.EmailFindRequest;
import org.mentalk.auth.request.LocalLoginRequest;
import org.mentalk.auth.request.PasswordResetRequest;
import org.mentalk.common.enums.ErrorCode;
import org.mentalk.common.response.ApiResponse;
import org.mentalk.common.security.PrincipalDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/check")
    public ResponseEntity<ApiResponse> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !authentication.getPrincipal().equals("anonymousUser")) {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

            return ResponseEntity.ok()
                                 .body(ApiResponse.success(Map.of("role", principal.role().name())));
        } else {
            return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getStatus())
                                 .body(ApiResponse.failure(ErrorCode.UNAUTHORIZED.getCode(),
                                                           ErrorCode.UNAUTHORIZED.getMessage()));
        }
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
                             .body(ApiResponse.success(responseData));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("access_token", "")
                                                    .httpOnly(true)
                                                    .path("/")
                                                    .maxAge(0)
                                                    .build();

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                             .body(ApiResponse.success(null));
    }

    @PostMapping("/email/exists")
    public ResponseEntity<ApiResponse> checkEmailExists(@RequestBody @Valid EmailCheckRequest request) {
        boolean exists = authService.isEmailExists(request.email());

        Map<String, Boolean> responseData = new HashMap<>();
        responseData.put("exists", exists);

        return ResponseEntity.ok()
                             .body(ApiResponse.success(responseData));
    }

    @PostMapping("/email/find")
    public ResponseEntity<ApiResponse> findEmail(@RequestBody @Valid EmailFindRequest request) {
        EmailDto emailDto = authService.findEmail(request.phoneNumber());

        return ResponseEntity.ok()
                             .body(ApiResponse.success(emailDto));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        authService.resetPassword(request.email(), request.password());

        return ResponseEntity.ok()
                             .body(ApiResponse.success(null));
    }
}
