package org.mentalk.utils;

import org.mentalk.auth.dto.request.EmailCheckRequest;
import org.mentalk.auth.dto.request.LocalLoginRequest;
import org.mentalk.member.dto.request.SignupRequest;

public class RequestFactory {

    public static SignupRequest signupRequest(Value<String> email, Value<String> password,
                                              Value<String> name, Value<String> phoneNumber) {
        return new SignupRequest(email.orElse("user@mentalk.com"), password.orElse("password"),
                                 name.orElse("user"), phoneNumber.orElse("01012345678"));
    }

    public static SignupRequest signupRequestWithDefaults() {
        return new SignupRequest("user@mentalk.com", "password", "user", "01012345678");
    }

    public static EmailCheckRequest emailCheckRequest(Value<String> email) {
        return new EmailCheckRequest(email.orElse("user@mentalk.com"));
    }

    public static EmailCheckRequest emailCheckRequestWithDefaults() {
        return new EmailCheckRequest("user@mentalk.com");
    }

    public static LocalLoginRequest localLoginRequest(Value<String> email, Value<String> password) {
        return new LocalLoginRequest(email.orElse("user@mentalk.com"), password.orElse("password"));
    }

    public static LocalLoginRequest localLoginRequestWithDefaults() {
        return new LocalLoginRequest("user@mentalk.com", "password");
    }
}
