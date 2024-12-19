package org.mentalk.auth.request;

import jakarta.validation.constraints.NotBlank;

public record LocalLoginRequest(@NotBlank String email,
                                @NotBlank String password) {
}
