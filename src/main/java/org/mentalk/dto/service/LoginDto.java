package org.mentalk.dto.service;

import org.mentalk.dto.request.LoginRequest;

public record LoginDto(String email, String password) {

    public static LoginDto of(LoginRequest request) {
        return new LoginDto(request.email(), request.password());
    }
}
