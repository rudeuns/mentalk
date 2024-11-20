package org.mentalk.dto;

public record AuthRequest() {

    public record Email(String email) {
    }

    public record PhoneNumber(String phoneNumber) {
    }

    public record Login(String email, String password) {
    }
}
