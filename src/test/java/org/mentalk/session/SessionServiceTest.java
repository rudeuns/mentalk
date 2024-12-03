package org.mentalk.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mentalk.common.enums.ErrorCode;
import org.mentalk.common.exception.ApiException;
import org.mentalk.member.MemberRepository;
import org.mentalk.member.domain.Member;
import org.mentalk.session.domain.Session;
import org.mentalk.session.dto.SessionDto;
import org.mentalk.session.dto.SessionIdDto;
import org.mentalk.utils.DtoFactory;
import org.mentalk.utils.EntityFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    @DisplayName("[세션 생성] 성공")
    void createSession_whenSuccess() {
        // given
        SessionDto sessionDto = DtoFactory.sessionDtoWithDefaults();

        Member mentor = EntityFactory.mentorWithDefaults();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(mentor));

        Session session = EntityFactory.sessionWithDefaults();
        given(sessionRepository.save(any(Session.class))).willReturn(session);

        // when
        SessionIdDto sessionIdDto = sessionService.createSession(sessionDto);

        // then
        assertThat(sessionIdDto.id()).isEqualTo(session.getId());

        verify(memberRepository, times(1)).findById(anyLong());
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    @DisplayName("[세션 생성] 멘토 회원을 찾을 수 없는 경우 -> 예외 발생")
    void createSession_whenMentorNotFound() {
        // given
        SessionDto sessionDto = DtoFactory.sessionDtoWithDefaults();

        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sessionService.createSession(sessionDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MENTOR_NOT_FOUND);

        verify(memberRepository, times(1)).findById(anyLong());
        verify(sessionRepository, never()).save(any(Session.class));
    }
}