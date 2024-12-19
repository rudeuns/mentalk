package org.mentalk.member.dto;

import org.mentalk.member.request.SignupRequest;

public record SignupDto(String email,
                        String password,
                        String name,
                        String phoneNumber) {

    public static SignupDto of(SignupRequest request) {
        return new SignupDto(request.email(), request.password(), request.name(),
                             request.phoneNumber());
    }
}
