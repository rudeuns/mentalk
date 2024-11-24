package org.mentalk.dto.service;

import org.mentalk.domain.Member;
import org.mentalk.dto.request.MemberCreateRequest;
import org.mentalk.enums.Role;

public record MemberDto(String email,
                        String password,
                        String username,
                        String phoneNumber,
                        Role role) {

    public static MemberDto of(MemberCreateRequest request) {
        return new MemberDto(request.email(),
                             request.password(),
                             request.username(),
                             request.phoneNumber(),
                             request.role());
    }

    public Member toEntity(String encodedPassword) {
        return Member.builder()
                     .email(email)
                     .password(encodedPassword)
                     .username(username)
                     .phoneNumber(phoneNumber)
                     .role(role)
                     .build();
    }
}
