package org.mentalk.auth.dto;

import org.mentalk.auth.domain.LocalAccount;
import org.mentalk.member.domain.Member;

public record LocalAccountDto(Member member,
                              String email,
                              String password) {

    public static LocalAccountDto of(Member member, String email, String password) {
        return new LocalAccountDto(member, email, password);
    }

    public LocalAccount toEntity(String hashedPassword) {
        return LocalAccount.builder()
                           .member(member)
                           .email(email)
                           .hashedPassword(hashedPassword)
                           .build();
    }
}
