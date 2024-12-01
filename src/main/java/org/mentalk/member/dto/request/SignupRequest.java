package org.mentalk.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(@NotBlank @Email String email,
                            @NotBlank String password,
                            @NotBlank String name,
                            @NotBlank @Pattern(regexp = "^\\+?[0-9]{10,15}$") String phoneNumber) {
}
