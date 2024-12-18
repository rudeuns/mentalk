package org.mentalk.auth.dto;

import org.mentalk.common.enums.Role;

public record JwtDto(String token, Role role) {

    public static JwtDto of(String token, Role role) {
        return new JwtDto(token, role);
    }
}
