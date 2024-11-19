package org.mentalk.controller;

import lombok.RequiredArgsConstructor;
import org.mentalk.dto.ApiResponse;
import org.mentalk.dto.MemberRequest;
import org.mentalk.service.MemberService;
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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerMember(
            @RequestBody MemberRequest.Register registerDto) {
        memberService.registerMember(registerDto);
        return ResponseEntity.ok()
                             .body(ApiResponse.success("회원가입이 성공적으로 완료되었습니다."));
    }
}
