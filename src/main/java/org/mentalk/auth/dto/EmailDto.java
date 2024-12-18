package org.mentalk.auth.dto;

public record EmailDto(String email) {

    public static EmailDto of(String email) {
        return new EmailDto(email);
    }
}
