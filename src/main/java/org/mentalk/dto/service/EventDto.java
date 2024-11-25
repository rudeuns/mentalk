package org.mentalk.dto.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mentalk.domain.Event;
import org.mentalk.domain.Member;
import org.mentalk.dto.request.EventCreateRequest;

public record EventDto(@NotBlank String title,
                       String description,
                       String content,
                       String thumbnailUrl,
                       @NotNull Long mentorId) {

    public static EventDto of(EventCreateRequest request, Long mentorId) {
        return new EventDto(request.title(),
                            request.description(),
                            request.content(),
                            request.thumbnailUrl(),
                            mentorId);
    }

    public Event toEntity(Member mentor) {
        return Event.builder()
                    .title(title)
                    .description(description)
                    .content(content)
                    .thumbnailUrl(thumbnailUrl)
                    .mentor(mentor)
                    .build();
    }
}

