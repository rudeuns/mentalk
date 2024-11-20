package org.mentalk.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.config.SecurityConfig;
import org.mentalk.dto.AuthRequest;
import org.mentalk.dto.AuthRequest.Email;
import org.mentalk.dto.AuthRequest.Login;
import org.mentalk.dto.AuthRequest.PhoneNumber;
import org.mentalk.dto.JwtDto;
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

    /**
     * /api/auth/check-email Test
     */

    @Test
    @DisplayName("이메일 중복 아닌 경우 - 200 응답")
    void validateEmail_whenNotDuplicated() throws Exception {
        // given
        AuthRequest.Email emailDto = new Email("test@test.com");
        doNothing().when(authService)
                   .validateEmailDuplicate(any(AuthRequest.Email.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/check-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDto))
        );

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andDo(print());
    }

    @Test
    @DisplayName("이메일 중복인 경우 - 409(C001) 응답")
    void validateEmail_whenDuplicated() throws Exception {
        // given
        AuthRequest.Email emailDto = new Email("test@test.com");
        doThrow(new ApiException(ErrorCode.EMAIL_DUPLICATED))
                .when(authService)
                .validateEmailDuplicate(any(AuthRequest.Email.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/check-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDto))
        );

        // then
        actions.andExpect(status().isConflict());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(jsonPath("$.payload.code").value(ErrorCode.EMAIL_DUPLICATED.getCode()));
        actions.andDo(print());
    }

    /**
     * /api/auth/check-phone Test
     */

    @Test
    @DisplayName("전화번호 중복 아닌 경우 - 200 응답")
    void validatePhoneNumber_whenNotDuplicated() throws Exception {
        // given
        AuthRequest.PhoneNumber phoneNumberDto = new PhoneNumber("01012345678");
        doNothing().when(authService)
                   .validatePhoneNumberDuplicate(any(AuthRequest.PhoneNumber.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/check-phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(phoneNumberDto))
        );

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andDo(print());
    }

    @Test
    @DisplayName("전화번호 중복인 경우 - 409(C002) 응답")
    void validatePhoneNumber_whenDuplicated() throws Exception {
        // given
        AuthRequest.PhoneNumber phoneNumberDto = new PhoneNumber("01012345678");
        doThrow(new ApiException(ErrorCode.PHONE_NUMBER_DUPLICATED))
                .when(authService)
                .validatePhoneNumberDuplicate(any(AuthRequest.PhoneNumber.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/check-phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(phoneNumberDto))
        );

        // then
        actions.andExpect(status().isConflict());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(jsonPath("$.payload.code").value(ErrorCode.PHONE_NUMBER_DUPLICATED.getCode()));
        actions.andDo(print());
    }

    /**
     * /api/auth/login Test
     */

    @Test
    @DisplayName("로그인 성공 - 200 응답")
    void login_whenSuccess() throws Exception {
        // given
        AuthRequest.Login loginDto = new Login("test@test.com", "testPassword");
        JwtDto jwtDto = new JwtDto("jwtToken");
        given(authService.login(any(AuthRequest.Login.class))).willReturn(jwtDto);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))
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
        AuthRequest.Login loginDto = new Login("test@test.com", "testPassword");
        given(authService.login(any(AuthRequest.Login.class)))
                .willThrow(new ApiException(ErrorCode.INVALID_EMAIL));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))
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
        AuthRequest.Login loginDto = new Login("test@test.com", "testPassword");
        given(authService.login(any(AuthRequest.Login.class)))
                .willThrow(new ApiException(ErrorCode.INVALID_PASSWORD));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))
        );

        // then
        actions.andExpect(status().isBadRequest());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(jsonPath("$.payload.code").value(ErrorCode.INVALID_PASSWORD.getCode()));
        actions.andDo(print());
    }
}