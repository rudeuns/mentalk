package org.mentalk.member;

import jakarta.validation.Valid;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mentalk.auth.dto.JwtDto;
import org.mentalk.common.response.ApiResponse;
import org.mentalk.common.security.PrincipalDetails;
import org.mentalk.member.dto.SignupDto;
import org.mentalk.member.request.SignupRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse> signup(@RequestBody @Valid SignupRequest request) {
        SignupDto signupDto = SignupDto.of(request);

        memberService.signup(signupDto);

        return ResponseEntity.ok()
                             .body(ApiResponse.success(null));
    }

    @PutMapping("/role/mentor")
    public ResponseEntity<ApiResponse> changeRoleToMentor(@AuthenticationPrincipal PrincipalDetails principal) {
        Long memberId = principal.id();

        JwtDto jwtDto = memberService.changeRoleToMentor(memberId);

        ResponseCookie jwtCookie = ResponseCookie.from("access_token", jwtDto.token())
                                                 .httpOnly(true)
                                                 .path("/")
                                                 .maxAge(Duration.ofDays(1))
                                                 .build();

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                             .body(ApiResponse.success(Map.of("role", jwtDto.role().name())));
    }
}
