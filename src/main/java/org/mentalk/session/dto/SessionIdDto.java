package org.mentalk.session.dto;

public record SessionIdDto(Long id) {

    public static SessionIdDto of(Long id) {
        return new SessionIdDto(id);
    }
}
