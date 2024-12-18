package org.mentalk.utils;

import org.mentalk.auth.dto.EmailDto;
import org.mentalk.auth.dto.JwtDto;
import org.mentalk.auth.dto.LocalAccountDto;
import org.mentalk.auth.dto.LocalLoginDto;
import org.mentalk.common.enums.Role;
import org.mentalk.common.enums.SessionType;
import org.mentalk.member.dto.SignupDto;
import org.mentalk.session.dto.SessionDto;
import org.mentalk.session.dto.SessionIdDto;

public class DtoFactory {

    public static SignupDto signupDtoWithDefaults() {
        return new SignupDto("user@mentalk.com", "password", "user", "01012345678");
    }

    public static LocalAccountDto localAccountDtoWithDefaults() {
        return new LocalAccountDto(EntityFactory.memberWithDefaults(), "user@mentalk.com",
                                   "password");
    }

    public static LocalLoginDto localLoginDtoWithDefaults() {
        return new LocalLoginDto("user@mentalk.com", "password");
    }

    public static JwtDto jwtDtoWithDefaults() {
        return new JwtDto("accessToken", Role.USER);
    }

    public static SessionDto sessionDtoWithDefaults() {
        return new SessionDto(SessionType.MENTORING, "Session Title", "Session Content", 1L);
    }

    public static SessionIdDto sessionIdDtoWithDefaults() {
        return new SessionIdDto(1L);
    }

    public static EmailDto emailDtoWithDefaults() {
        return new EmailDto("user@mentalk.com");
    }
}
