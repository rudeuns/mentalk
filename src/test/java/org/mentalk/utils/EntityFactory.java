package org.mentalk.utils;

import org.mentalk.auth.domain.LocalAccount;
import org.mentalk.common.enums.Role;
import org.mentalk.member.domain.Member;

public class EntityFactory {

    public static Member member(Value<String> name, Value<String> phoneNumber,
                                Value<Role> role) {
        return Member.builder()
                     .name(name.orElse("user"))
                     .phoneNumber(phoneNumber.orElse("01012345678"))
                     .role(role.orElse(Role.USER))
                     .build();
    }

    public static Member memberWithId(Value<Long> id, Value<String> name, Value<String> phoneNumber,
                                      Value<Role> role) {
        return Member.builder()
                     .id(id.orElse(1L))
                     .name(name.orElse("user"))
                     .phoneNumber(phoneNumber.orElse("01012345678"))
                     .role(role.orElse(Role.USER))
                     .build();
    }

    public static Member memberWithDefaults() {
        return Member.builder()
                     .id(1L)
                     .name("user")
                     .phoneNumber("01012345678")
                     .role(Role.USER)
                     .build();
    }

    public static LocalAccount localAccount(Value<Member> member,
                                            Value<String> email, Value<String> hashedPassword) {
        return LocalAccount.builder()
                           .member(member.orElse(EntityFactory.memberWithDefaults()))
                           .email(email.orElse("user@mentalk.com"))
                           .hashedPassword(hashedPassword.orElse("hashedPassword"))
                           .build();
    }

    public static LocalAccount localAccountWithId(Value<Long> id, Value<Member> member,
                                                  Value<String> email,
                                                  Value<String> hashedPassword) {
        return LocalAccount.builder()
                           .id(id.orElse(1L))
                           .member(member.orElse(EntityFactory.memberWithDefaults()))
                           .email(email.orElse("user@mentalk.com"))
                           .hashedPassword(hashedPassword.orElse("hashedPassword"))
                           .build();
    }

    public static LocalAccount localAccountWithDefaults() {
        return LocalAccount.builder()
                           .id(1L)
                           .member(EntityFactory.memberWithDefaults())
                           .email("user@mentalk.com")
                           .hashedPassword("hashedPassword")
                           .build();
    }
}
