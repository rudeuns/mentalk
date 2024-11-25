package org.mentalk.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mentalk.dto.request.EventCreateRequest;
import org.mentalk.dto.response.ApiResponse;
import org.mentalk.dto.service.EventDto;
import org.mentalk.dto.service.EventIdDto;
import org.mentalk.security.PrincipalDetails;
import org.mentalk.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<ApiResponse> createEvent(@Valid @RequestBody EventCreateRequest request,
                                                   @AuthenticationPrincipal PrincipalDetails principal) {
        Long mentorId = principal.id();
        EventDto eventDto = EventDto.of(request, mentorId);
        EventIdDto eventIdDto = eventService.createEvent(eventDto);
        return ResponseEntity.ok()
                             .body(ApiResponse.success("이벤트가 성공적으로 생성되었습니다.", eventIdDto));
    }
}
