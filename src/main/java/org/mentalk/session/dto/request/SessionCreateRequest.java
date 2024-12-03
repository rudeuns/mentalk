package org.mentalk.session.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mentalk.common.enums.SessionType;

public record SessionCreateRequest(@NotNull SessionType sessionType,
                                   @NotBlank String title,
                                   @NotBlank String content) {
}
