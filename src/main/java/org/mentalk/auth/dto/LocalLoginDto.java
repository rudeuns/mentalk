package org.mentalk.auth.dto;

import org.mentalk.auth.dto.request.LocalLoginRequest;

public record LocalLoginDto(String email, String password) {

    public static LocalLoginDto of(LocalLoginRequest request) {
        return new LocalLoginDto(request.email(), request.password());
    }
}
