package org.mentalk.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mentalk.common.dto.ApiResponse;
import org.mentalk.member.dto.SignupDto;
import org.mentalk.member.dto.request.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
                             .body(ApiResponse.success("회원가입이 성공적으로 완료되었습니다."));
    }
}
