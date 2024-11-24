package org.mentalk.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.config.SecurityConfig;
import org.mentalk.dto.request.EmailCheckRequest;
import org.mentalk.dto.request.LoginRequest;
import org.mentalk.dto.request.PhoneNumberCheckRequest;
import org.mentalk.dto.service.EmailDto;
import org.mentalk.dto.service.JwtDto;
import org.mentalk.dto.service.LoginDto;
import org.mentalk.dto.service.PhoneNumberDto;
import org.mentalk.enums.ErrorCode;
import org.mentalk.exception.ApiException;
import org.mentalk.security.JwtUtil;
import org.mentalk.service.AuthService;
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

    private EmailCheckRequest emailCheckRequest;
    private PhoneNumberCheckRequest phoneNumberCheckRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        emailCheckRequest = new EmailCheckRequest("test@test.com");
        phoneNumberCheckRequest = new PhoneNumberCheckRequest("01012345678");
        loginRequest = new LoginRequest("test@test.com", "testPassword");
    }

    /**
     * /api/auth/check-email Test
     */

    @Test
    @DisplayName("이메일 중복 아닌 경우 - 200 응답")
    void checkEmail_whenNotDuplicate() throws Exception {
        // given
        doNothing().when(authService)
                   .checkEmailDuplicate(any(EmailDto.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/check-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailCheckRequest))
        );

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andDo(print());
    }

    @Test
    @DisplayName("이메일 중복인 경우 - 409(C001) 응답")
    void checkEmail_whenDuplicate() throws Exception {
        // given
        doThrow(new ApiException(ErrorCode.EMAIL_DUPLICATE))
                .when(authService)
                .checkEmailDuplicate(any(EmailDto.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/check-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailCheckRequest))
        );

        // then
        actions.andExpect(status().isConflict());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(jsonPath("$.payload.code").value(ErrorCode.EMAIL_DUPLICATE.getCode()));
        actions.andDo(print());
    }

    /**
     * /api/auth/check-phone-number Test
     */

    @Test
    @DisplayName("전화번호 중복 아닌 경우 - 200 응답")
    void checkPhoneNumber_whenNotDuplicate() throws Exception {
        // given
        doNothing().when(authService)
                   .checkPhoneNumberDuplicate(any(PhoneNumberDto.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/check-phone-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(phoneNumberCheckRequest))
        );

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andDo(print());
    }

    @Test
    @DisplayName("전화번호 중복인 경우 - 409(C002) 응답")
    void checkPhoneNumber_whenDuplicate() throws Exception {
        // given
        doThrow(new ApiException(ErrorCode.PHONE_NUMBER_DUPLICATE))
                .when(authService)
                .checkPhoneNumberDuplicate(any(PhoneNumberDto.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/check-phone-number")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(phoneNumberCheckRequest))
        );

        // then
        actions.andExpect(status().isConflict());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(
                jsonPath("$.payload.code").value(ErrorCode.PHONE_NUMBER_DUPLICATE.getCode()));
        actions.andDo(print());
    }

    /**
     * /api/auth/login Test
     */

    @Test
    @DisplayName("로그인 성공 - 200 응답")
    void login_whenSuccess() throws Exception {
        // given
        JwtDto jwtDto = mock(JwtDto.class);
        given(authService.login(any(LoginDto.class))).willReturn(jwtDto);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
        );

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andDo(print());
    }

    @Test
    @DisplayName("로그인 이메일 잘못 입력 - 400(B001) 응답")
    void login_whenInvalidEmail() throws Exception {
        // given
        given(authService.login(any(LoginDto.class)))
                .willThrow(new ApiException(ErrorCode.INVALID_EMAIL));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
        );

        // then
        actions.andExpect(status().isBadRequest());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(jsonPath("$.payload.code").value(ErrorCode.INVALID_EMAIL.getCode()));
        actions.andDo(print());
    }


    @Test
    @DisplayName("로그인 비밀번호 잘못 입력 - 400(B002) 응답")
    void login_whenInvalidPassword() throws Exception {
        // given
        given(authService.login(any(LoginDto.class)))
                .willThrow(new ApiException(ErrorCode.INVALID_PASSWORD));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
        );

        // then
        actions.andExpect(status().isBadRequest());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(jsonPath("$.payload.code").value(ErrorCode.INVALID_PASSWORD.getCode()));
        actions.andDo(print());
    }
}