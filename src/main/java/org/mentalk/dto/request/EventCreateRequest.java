package org.mentalk.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EventCreateRequest(@NotBlank String title,
                                 String description,
                                 String content,
                                 String thumbnailUrl) {

}
