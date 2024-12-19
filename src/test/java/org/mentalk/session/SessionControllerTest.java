package org.mentalk.session;

import static org.mentalk.common.enums.ErrorCode.FORBIDDEN;
import static org.mentalk.common.enums.ErrorCode.UNAUTHORIZED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.annotation.WithCustomMockMentor;
import org.mentalk.annotation.WithCustomMockUser;
import org.mentalk.common.config.SecurityConfig;
import org.mentalk.common.security.JwtUtil;
import org.mentalk.session.dto.SessionDto;
import org.mentalk.session.dto.SessionIdDto;
import org.mentalk.session.request.SessionCreateRequest;
import org.mentalk.utils.DtoFactory;
import org.mentalk.utils.RequestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(SessionController.class)
@Import(SecurityConfig.class)
@MockBean(JwtUtil.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionService sessionService;

    @Test
    @WithCustomMockMentor
    @DisplayName("[세션 생성] 멘토 회원일 때 성공 -> 200 응답")
    void createSession_whenMemberIsMentor() throws Exception {
        // given
        SessionCreateRequest request = RequestFactory.sessionCreateRequestWithDefaults();

        SessionIdDto sessionIdDto = DtoFactory.sessionIdDtoWithDefaults();
        given(sessionService.createSession(any(SessionDto.class))).willReturn(sessionIdDto);

        // when
        ResultActions result = mockMvc.perform(
                post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.success").value(true));
        result.andExpect(jsonPath("$.payload.data.id").value(sessionIdDto.id()));
        result.andDo(print());
    }

    @Test
    @WithCustomMockUser
    @DisplayName("[세션 생성] 일반 회원일 때 실패 -> 403 응답")
    void createSession_whenMemberIsUser() throws Exception {
        // given
        SessionCreateRequest request = RequestFactory.sessionCreateRequestWithDefaults();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isForbidden());
        result.andExpect(jsonPath("$.success").value(false));
        result.andExpect(jsonPath("$.payload.code").value(FORBIDDEN.getCode()));
        result.andDo(print());
    }

    @Test
    @DisplayName("[세션 생성] 인증 정보 없을 때 실패 -> 401 응답")
    void createSession_whenUnauthorized() throws Exception {
        // given
        SessionCreateRequest request = RequestFactory.sessionCreateRequestWithDefaults();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isUnauthorized());
        result.andExpect(jsonPath("$.success").value(false));
        result.andExpect(jsonPath("$.payload.code").value(UNAUTHORIZED.getCode()));
        result.andDo(print());
    }
}