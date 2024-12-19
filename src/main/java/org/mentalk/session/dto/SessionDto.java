package org.mentalk.session.dto;

import org.mentalk.common.enums.SessionType;
import org.mentalk.member.domain.Member;
import org.mentalk.session.domain.Session;
import org.mentalk.session.request.SessionCreateRequest;

public record SessionDto(SessionType sessionType,
                         String title,
                         String content,
                         Long mentorId) {

    public static SessionDto of(SessionCreateRequest request, Long mentorId) {
        return new SessionDto(request.sessionType(), request.title(), request.content(), mentorId);
    }

    public Session toEntity(Member mentor) {
        return Session.builder()
                      .mentor(mentor)
                      .sessionType(sessionType)
                      .title(title)
                      .content(content)
                      .build();
    }
}
