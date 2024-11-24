package org.mentalk.dto.service;

import org.mentalk.dto.request.PhoneNumberCheckRequest;

public record PhoneNumberDto(String phoneNumber) {

    public static PhoneNumberDto of(PhoneNumberCheckRequest request) {
        return new PhoneNumberDto(request.phoneNumber());
    }
}
