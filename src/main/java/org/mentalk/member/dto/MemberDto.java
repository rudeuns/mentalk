package org.mentalk.member.dto;

import org.mentalk.common.enums.Role;
import org.mentalk.member.domain.Member;

public record MemberDto(String name,
                        String phoneNumber,
                        Role role) {

    public static MemberDto of(String name, String phoneNumber) {
        return new MemberDto(name, phoneNumber, Role.USER);
    }

    public Member toEntity() {
        return Member.builder()
                     .name(name)
                     .phoneNumber(phoneNumber)
                     .role(role)
                     .build();
    }
}
