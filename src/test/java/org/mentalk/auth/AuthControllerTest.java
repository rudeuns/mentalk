package org.mentalk.auth;

import static org.mentalk.common.enums.ErrorCode.ALREADY_EMAIL_IN_USE;
import static org.mentalk.common.enums.ErrorCode.EMAIL_NOT_FOUND;
import static org.mentalk.common.enums.ErrorCode.INVALID_PASSWORD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.auth.dto.JwtDto;
import org.mentalk.auth.dto.LocalLoginDto;
import org.mentalk.auth.dto.request.EmailCheckRequest;
import org.mentalk.auth.dto.request.LocalLoginRequest;
import org.mentalk.common.config.SecurityConfig;
import org.mentalk.common.exception.ApiException;
import org.mentalk.common.security.JwtUtil;
import org.mentalk.utils.DtoFactory;
import org.mentalk.utils.RequestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@MockBean(JwtUtil.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("[이메일 중복 확인] 중복 아닌 경우 -> 200 응답")
    void whenEmailNotInUse() throws Exception {
        // given
        EmailCheckRequest request = RequestFactory.emailCheckRequestWithDefaults();

        doNothing().when(authService).checkEmailInUse(anyString());

        // when
        ResultActions result = mockMvc.perform(
                post("/api/auth/email/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.success").value(true));
        result.andDo(print());
    }

    @Test
    @DisplayName("[이메일 중복 확인] 중복인 경우 -> 409 응답")
    void whenEmailInUse() throws Exception {
        // given
        EmailCheckRequest request = RequestFactory.emailCheckRequestWithDefaults();

        doThrow(new ApiException(ALREADY_EMAIL_IN_USE))
                .when(authService).checkEmailInUse(anyString());

        // when
        ResultActions result = mockMvc.perform(
                post("/api/auth/email/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isConflict());
        result.andExpect(jsonPath("$.success").value(false));
        result.andExpect(jsonPath("$.payload.code").value(ALREADY_EMAIL_IN_USE.getCode()));
        result.andDo(print());
    }

    @Test
    @DisplayName("[로컬 로그인] 성공 -> 200 응답, 헤더 토큰")
    void localLogin_whenSuccess() throws Exception {
        // given
        LocalLoginRequest request = RequestFactory.localLoginRequestWithDefaults();

        JwtDto jwtDto = DtoFactory.jwtDtoWithDefaults();
        given(authService.localLogin(any(LocalLoginDto.class))).willReturn(jwtDto);

        // when
        ResultActions result = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.success").value(true));
        result.andExpect(header().string("Authorization", "Bearer " + jwtDto.token()));
        result.andDo(print());
    }

    @Test
    @DisplayName("[로컬 로그인] 이메일을 찾을 수 없는 경우 -> 404 응답")
    void localLogin_whenEmailNotFound() throws Exception {
        // given
        LocalLoginRequest request = RequestFactory.localLoginRequestWithDefaults();

        given(authService.localLogin(any(LocalLoginDto.class)))
                .willThrow(new ApiException(EMAIL_NOT_FOUND));

        // when
        ResultActions result = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.success").value(false));
        result.andExpect(jsonPath("$.payload.code").value(EMAIL_NOT_FOUND.getCode()));
        result.andDo(print());
    }

    @Test
    @DisplayName("[로컬 로그인] 비밀번호가 일치하지 않는 경우 -> 401 응답")
    void localLogin_whenInvalidPassword() throws Exception {
        // given
        LocalLoginRequest request = RequestFactory.localLoginRequestWithDefaults();

        given(authService.localLogin(any(LocalLoginDto.class)))
                .willThrow(new ApiException(INVALID_PASSWORD));

        // when
        ResultActions result = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isUnauthorized());
        result.andExpect(jsonPath("$.success").value(false));
        result.andExpect(jsonPath("$.payload.code").value(INVALID_PASSWORD.getCode()));
        result.andDo(print());
    }
}