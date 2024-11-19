package org.mentalk.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mentalk.domain.Member;
import org.mentalk.enums.Role;

public record MemberRequest() {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Register {
        private String email;
        private String password;
        private String username;
        private String phoneNumber;
        private Role role;

        public Member toEntity(String encodedPassword) {
            return Member.builder()
                         .email(this.email)
                         .password(encodedPassword)
                         .username(this.username)
                         .phoneNumber(this.phoneNumber)
                         .role(this.role)
                         .build();
        }
    }
}
