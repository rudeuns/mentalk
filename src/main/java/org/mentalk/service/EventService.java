package org.mentalk.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mentalk.domain.Event;
import org.mentalk.domain.Member;
import org.mentalk.dto.service.EventDto;
import org.mentalk.dto.service.EventIdDto;
import org.mentalk.enums.ErrorCode;
import org.mentalk.exception.ApiException;
import org.mentalk.repository.EventRepository;
import org.mentalk.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;

    @Transactional
    public EventIdDto createEvent(@Valid EventDto eventDto) {
        Member mentor = memberRepository.findById(eventDto.mentorId())
                                        .orElseThrow(() -> new ApiException(
                                                ErrorCode.MENTOR_NOT_FOUND));

        Event event = eventDto.toEntity(mentor);
        Event savedEvent = eventRepository.save(event);
        return new EventIdDto(savedEvent.getId());
    }
}
