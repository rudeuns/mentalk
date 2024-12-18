package org.mentalk.session;

import lombok.RequiredArgsConstructor;
import org.mentalk.common.enums.ErrorCode;
import org.mentalk.common.exception.ApiException;
import org.mentalk.member.MemberRepository;
import org.mentalk.member.domain.Member;
import org.mentalk.session.domain.Session;
import org.mentalk.session.dto.SessionDto;
import org.mentalk.session.dto.SessionIdDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final MemberRepository memberRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public SessionIdDto createSession(SessionDto sessionDto) {
        Member mentor = memberRepository.findById(sessionDto.mentorId()).orElseThrow(
                () -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        Session session = sessionRepository.save(sessionDto.toEntity(mentor));

        return SessionIdDto.of(session.getId());
    }
}
