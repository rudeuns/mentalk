package org.mentalk.dto.service;

import org.mentalk.dto.request.EmailCheckRequest;

public record EmailDto(String email) {

    public static EmailDto of(EmailCheckRequest request) {
        return new EmailDto(request.email());
    }
}
