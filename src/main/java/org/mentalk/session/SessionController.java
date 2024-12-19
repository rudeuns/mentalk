package org.mentalk.session;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mentalk.common.response.ApiResponse;
import org.mentalk.common.security.PrincipalDetails;
import org.mentalk.session.dto.SessionDto;
import org.mentalk.session.dto.SessionIdDto;
import org.mentalk.session.request.SessionCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<ApiResponse> createSession(
            @RequestBody @Valid SessionCreateRequest request,
            @AuthenticationPrincipal PrincipalDetails principal) {

        SessionDto sessionDto = SessionDto.of(request, principal.id());

        SessionIdDto sessionIdDto = sessionService.createSession(sessionDto);

        return ResponseEntity.ok().body(ApiResponse.success("세션이 성공적으로 생성되었습니다.", sessionIdDto));
    }
}
