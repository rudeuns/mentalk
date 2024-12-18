package org.mentalk.utils;

import org.mentalk.auth.domain.LocalAccount;
import org.mentalk.common.enums.Role;
import org.mentalk.common.enums.SessionType;
import org.mentalk.member.domain.Member;
import org.mentalk.session.domain.Session;

public class EntityFactory {

    public static Member member(Value<String> name, Value<String> phoneNumber,
                                Value<Role> role) {
        return Member.builder()
                     .name(name.orElse("user"))
                     .phoneNumber(phoneNumber.orElse("01012345678"))
                     .role(role.orElse(Role.USER))
                     .build();
    }

    public static Member memberWithDefaults() {
        return Member.builder()
                     .id(1L)
                     .name("user")
                     .phoneNumber("01012345678")
                     .role(Role.USER)
                     .build();
    }

    public static Member mentorWithDefaults() {
        return Member.builder()
                     .id(1L)
                     .name("mentor")
                     .phoneNumber("01012345678")
                     .role(Role.MENTOR)
                     .build();
    }

    public static LocalAccount localAccount(Value<Member> member,
                                            Value<String> email, Value<String> hashedPassword) {
        return LocalAccount.builder()
                           .member(member.orElse(EntityFactory.memberWithDefaults()))
                           .email(email.orElse("user@mentalk.com"))
                           .hashedPassword(hashedPassword.orElse("hashedPassword"))
                           .build();
    }

    public static LocalAccount localAccountWithDefaults() {
        return LocalAccount.builder()
                           .id(1L)
                           .member(EntityFactory.memberWithDefaults())
                           .email("user@mentalk.com")
                           .hashedPassword("hashedPassword")
                           .build();
    }

    public static Session session(Value<Member> mentor, Value<SessionType> sessionType,
                                  Value<String> title, Value<String> content) {
        return Session.builder()
                      .mentor(mentor.orElse(EntityFactory.mentorWithDefaults()))
                      .sessionType(sessionType.orElse(SessionType.MENTORING))
                      .title(title.orElse("Session Title"))
                      .content(content.orElse("Session Content"))
                      .build();
    }

    public static Session sessionWithDefaults() {
        return Session.builder()
                      .id(1L)
                      .mentor(EntityFactory.mentorWithDefaults())
                      .sessionType(SessionType.MENTORING)
                      .title("Session Title")
                      .content("Session Content")
                      .build();
    }
}
