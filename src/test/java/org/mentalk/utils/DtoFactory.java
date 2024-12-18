package org.mentalk.utils;

import org.mentalk.auth.dto.JwtDto;
import org.mentalk.auth.dto.LocalAccountDto;
import org.mentalk.auth.dto.LocalLoginDto;
import org.mentalk.common.enums.Role;
import org.mentalk.common.enums.SessionType;
import org.mentalk.member.domain.Member;
import org.mentalk.member.dto.MemberDto;
import org.mentalk.member.dto.SignupDto;
import org.mentalk.session.dto.SessionDto;
import org.mentalk.session.dto.SessionIdDto;

public class DtoFactory {

    public static MemberDto memberDto(Value<String> name, Value<String> phoneNumber,
                                      Value<Role> role) {
        return new MemberDto(name.orElse("user"), phoneNumber.orElse("01012345678"),
                             role.orElse(Role.USER));
    }

    public static MemberDto memberDtoWithDefaults() {
        return new MemberDto("user", "01012345678", Role.USER);
    }

    public static SignupDto signupDto(Value<String> email, Value<String> password,
                                      Value<String> name, Value<String> phoneNumber) {
        return new SignupDto(email.orElse("user@mentalk.com"), password.orElse("password"),
                             name.orElse("user"), phoneNumber.orElse("01012345678"));
    }

    public static SignupDto signupDtoWithDefaults() {
        return new SignupDto("user@mentalk.com", "password", "user", "01012345678");
    }

    public static LocalAccountDto localAccountDto(Value<Member> member, Value<String> email,
                                                  Value<String> password) {
        return new LocalAccountDto(member.orElse(EntityFactory.memberWithDefaults()),
                                   email.orElse("user@mentalk.com"), password.orElse("password"));
    }

    public static LocalAccountDto localAccountDtoWithDefaults() {
        return new LocalAccountDto(EntityFactory.memberWithDefaults(), "user@mentalk.com",
                                   "password");
    }

    public static LocalLoginDto localLoginDto(Value<String> email, Value<String> password) {
        return new LocalLoginDto(email.orElse("user@mentalk.com"), password.orElse("password"));
    }

    public static LocalLoginDto localLoginDtoWithDefaults() {
        return new LocalLoginDto("user@mentalk.com", "password");
    }

    public static JwtDto jwtDto(Value<String> token, Value<Role> role) {
        return new JwtDto(token.orElse("accessToken"), role.orElse(Role.USER));
    }

    public static JwtDto jwtDtoWithDefaults() {
        return new JwtDto("accessToken", Role.USER);
    }

    public static SessionDto sessionDto(Value<SessionType> sessionType, Value<String> title,
                                        Value<String> content, Value<Long> mentorId) {
        return new SessionDto(sessionType.orElse(SessionType.MENTORING),
                              title.orElse("Session Title"),
                              content.orElse("Session Content"), mentorId.orElse(1L));
    }

    public static SessionDto sessionDtoWithDefaults() {
        return new SessionDto(SessionType.MENTORING, "Session Title", "Session Content", 1L);
    }

    public static SessionIdDto sessionIdDtoWithDefaults() {
        return new SessionIdDto(1L);
    }
}
