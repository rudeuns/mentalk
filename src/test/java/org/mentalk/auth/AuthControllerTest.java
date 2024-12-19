package org.mentalk.auth;

import static org.mentalk.common.enums.ErrorCode.ACCOUNT_NOT_FOUND;
import static org.mentalk.common.enums.ErrorCode.EMAIL_NOT_FOUND;
import static org.mentalk.common.enums.ErrorCode.INVALID_PASSWORD;
import static org.mentalk.common.enums.ErrorCode.MEMBER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.auth.dto.EmailDto;
import org.mentalk.auth.dto.JwtDto;
import org.mentalk.auth.dto.LocalLoginDto;
import org.mentalk.auth.request.EmailFindRequest;
import org.mentalk.auth.request.LocalLoginRequest;
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
        result.andExpect(cookie().value("access_token", jwtDto.token()));
        result.andExpect(cookie().httpOnly("access_token", true));
        result.andExpect(jsonPath("$.payload.data.role").value(String.valueOf(jwtDto.role())));
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

    @Test
    @DisplayName("[이메일 찾기] 성공 -> 200 응답, 이메일 반환")
    void findEmail_whenSuccess() throws Exception {
        // given
        EmailFindRequest request = RequestFactory.emailFindRequestWithDefaults();

        EmailDto emailDto = DtoFactory.emailDtoWithDefaults();
        given(authService.findEmail(anyString())).willReturn(emailDto);

        // when
        ResultActions result = mockMvc.perform(
                post("/api/auth/email/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.success").value(true));
        result.andExpect(jsonPath("$.payload.data.email").value(emailDto.email()));
        result.andDo(print());
    }

    @Test
    @DisplayName("[이메일 찾기] 회원 정보를 찾을 수 없는 경우 -> 404 응답")
    void findEmail_whenMemberNotFound() throws Exception {
        // given
        EmailFindRequest request = RequestFactory.emailFindRequestWithDefaults();

        given(authService.findEmail(anyString())).willThrow(new ApiException(MEMBER_NOT_FOUND));

        // when
        ResultActions result = mockMvc.perform(
                post("/api/auth/email/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.success").value(false));
        result.andExpect(jsonPath("$.payload.code").value(MEMBER_NOT_FOUND.getCode()));
        result.andDo(print());
    }

    @Test
    @DisplayName("[이메일 찾기] 계정 정보를 찾을 수 없는 경우 -> 404 응답")
    void findEmail_whenAccountNotFound() throws Exception {
        // given
        EmailFindRequest request = RequestFactory.emailFindRequestWithDefaults();

        given(authService.findEmail(anyString())).willThrow(new ApiException(ACCOUNT_NOT_FOUND));

        // when
        ResultActions result = mockMvc.perform(
                post("/api/auth/email/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.success").value(false));
        result.andExpect(jsonPath("$.payload.code").value(ACCOUNT_NOT_FOUND.getCode()));
        result.andDo(print());
    }
}