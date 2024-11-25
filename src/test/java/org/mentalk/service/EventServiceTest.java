package org.mentalk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mentalk.domain.Event;
import org.mentalk.domain.Member;
import org.mentalk.dto.service.EventDto;
import org.mentalk.dto.service.EventIdDto;
import org.mentalk.enums.ErrorCode;
import org.mentalk.exception.ApiException;
import org.mentalk.repository.EventRepository;
import org.mentalk.repository.MemberRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private EventService eventService;

    /**
     * createEvent Test
     */
    @Test
    @DisplayName("이벤트 생성 성공 테스트")
    void createEvent_whenSuccess() {
        // given
        EventDto eventDto = new EventDto("-", null, null, null, 1L);

        Member member = mock(Member.class);
        given(memberRepository.findById(any(Long.class))).willReturn(Optional.of(member));

        Event event = Event.builder()
                           .id(1L)
                           .build();
        given(eventRepository.save(any(Event.class))).willReturn(event);

        // when
        EventIdDto eventIdDto = eventService.createEvent(eventDto);

        // then
        assertThat(eventIdDto.id()).isEqualTo(event.getId());

        // verify
        verify(memberRepository, times(1)).findById(any(Long.class));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("이벤트를 생성할 멘토 회원을 찾을 수 없는 경우 예외 테스트")
    void createEvent_whenMentorNotFound() {
        // given
        EventDto eventDto = new EventDto("-", null, null, null, 1L);

        given(memberRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> eventService.createEvent(eventDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MENTOR_NOT_FOUND);

        // verify
        verify(memberRepository, times(1)).findById(any(Long.class));
        verify(eventRepository, never()).save(any(Event.class));
    }
}