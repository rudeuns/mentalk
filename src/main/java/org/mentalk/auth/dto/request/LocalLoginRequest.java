package org.mentalk.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LocalLoginRequest(@NotBlank String email,
                                @NotBlank String password) {
}
