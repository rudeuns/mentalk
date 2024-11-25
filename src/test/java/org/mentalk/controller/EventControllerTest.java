package org.mentalk.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.annotation.WithCustomMockMentor;
import org.mentalk.annotation.WithCustomMockUser;
import org.mentalk.config.SecurityConfig;
import org.mentalk.dto.request.EventCreateRequest;
import org.mentalk.dto.service.EventDto;
import org.mentalk.dto.service.EventIdDto;
import org.mentalk.enums.ErrorCode;
import org.mentalk.exception.ApiException;
import org.mentalk.security.JwtUtil;
import org.mentalk.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = EventController.class)
@Import(SecurityConfig.class)
@MockBean(JwtUtil.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    /**
     * POST /api/events Test
     */
    @Test
    @WithCustomMockMentor
    @DisplayName("이벤트 생성 성공 테스트")
    void createEvent_whenSuccess() throws Exception {
        // given
        EventCreateRequest request = new EventCreateRequest("-", null, null, null);

        EventIdDto eventIdDto = mock(EventIdDto.class);
        given(eventService.createEvent(any(EventDto.class))).willReturn(eventIdDto);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andDo(print());
    }

    @Test
    @WithCustomMockMentor
    @DisplayName("이벤트의 title NotBlank 위반 - 400 응답")
    void createEvent_whenBlankTitle() throws Exception {
        // given
        EventCreateRequest request = new EventCreateRequest(null, null, null, null);

        EventIdDto eventIdDto = mock(EventIdDto.class);
        given(eventService.createEvent(any(EventDto.class))).willReturn(eventIdDto);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        actions.andExpect(status().isBadRequest());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(
                jsonPath("$.payload.code").value(ErrorCode.METHOD_ARG_NOT_VALID.getCode()));
        actions.andDo(print());
    }


    @Test
    @WithCustomMockMentor
    @DisplayName("이벤트 서비스에서 MENTOR_NOT_FOUND 에외 발생 - 404 응답")
    void createEvent_whenMentorNotFound() throws Exception {
        // given
        EventCreateRequest request = new EventCreateRequest("-", null, null, null);

        given(eventService.createEvent(any(EventDto.class))).willThrow(
                new ApiException(ErrorCode.MENTOR_NOT_FOUND));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        actions.andExpect(status().isNotFound());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(
                jsonPath("$.payload.code").value(ErrorCode.MENTOR_NOT_FOUND.getCode()));
        actions.andDo(print());
    }


    @Test
    @DisplayName("이벤트 생성 시 인증 정보 누락 - 401 응답")
    void createEvent_whenUnauthorized() throws Exception {
        // given
        EventCreateRequest request = new EventCreateRequest("-", null, null, null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        actions.andExpect(status().isUnauthorized());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(jsonPath("$.payload.code").value(ErrorCode.SC_UNAUTHORIZED.getCode()));
        actions.andDo(print());
    }

    @Test
    @WithCustomMockUser
    @DisplayName("일반 회원이 이벤트 생성 시도 - 403 응답")
    void createEvent_whenForbidden() throws Exception {
        // given
        EventCreateRequest request = new EventCreateRequest("-", null, null, null);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        actions.andExpect(status().isForbidden());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(jsonPath("$.payload.code").value(ErrorCode.SC_FORBIDDEN.getCode()));
        actions.andDo(print());
    }
}