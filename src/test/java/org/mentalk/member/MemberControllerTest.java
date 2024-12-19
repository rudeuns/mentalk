package org.mentalk.member;

import static org.mentalk.common.enums.ErrorCode.ALREADY_ACCOUNT_REGISTERED;
import static org.mentalk.common.enums.ErrorCode.ALREADY_EMAIL_IN_USE;
import static org.mentalk.common.enums.ErrorCode.METHOD_ARG_NOT_VALID;
import static org.mockito.BDDMockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.common.config.SecurityConfig;
import org.mentalk.common.exception.ApiException;
import org.mentalk.common.security.JwtUtil;
import org.mentalk.member.dto.SignupDto;
import org.mentalk.member.request.SignupRequest;
import org.mentalk.utils.RequestFactory;
import org.mentalk.utils.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MemberController.class)
@Import(SecurityConfig.class)
@MockBean(JwtUtil.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("[회원가입] 성공 -> 200 응답")
    void signup_whenSuccess() throws Exception {
        // given
        SignupRequest request = RequestFactory.signupRequestWithDefaults();

        doNothing().when(memberService).signup(any(SignupDto.class));

        // when
        ResultActions result = mockMvc.perform(
                post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.success").value(true));
        result.andDo(print());
    }

    @Test
    @DisplayName("[회원가입] 이미 가입된 회원 -> 409 응답")
    void signup_whenAlreadyRegistered() throws Exception {
        // given
        SignupRequest request = RequestFactory.signupRequestWithDefaults();

        doThrow(new ApiException(ALREADY_ACCOUNT_REGISTERED)).when(memberService)
                                                             .signup(any(SignupDto.class));

        // when
        ResultActions result = mockMvc.perform(
                post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isConflict());
        result.andExpect(jsonPath("$.success").value(false));
        result.andExpect(jsonPath("$.payload.code").value(ALREADY_ACCOUNT_REGISTERED.getCode()));
        result.andDo(print());
    }

    @Test
    @DisplayName("[회원가입] 이메일 중복 -> 409 응답")
    void signup_whenEmailInUse() throws Exception {
        // given
        SignupRequest request = RequestFactory.signupRequestWithDefaults();

        doThrow(new ApiException(ALREADY_EMAIL_IN_USE)).when(memberService)
                                                       .signup(any(SignupDto.class));

        // when
        ResultActions result = mockMvc.perform(
                post("/api/members")
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
    @DisplayName("[회원가입] 잘못된 이메일 형식 -> 400 응답")
    void signup_whenInvalidEmailFormat() throws Exception {
        // given
        SignupRequest request = RequestFactory.signupRequest(Value.of("email"), Value.defaults(),
                                                             Value.defaults(), Value.defaults());

        // when
        ResultActions result = mockMvc.perform(
                post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.success").value(false));
        result.andExpect(jsonPath("$.payload.code").value(METHOD_ARG_NOT_VALID.getCode()));
        result.andDo(print());
    }

    @Test
    @DisplayName("[회원가입] 잘못된 전화번호 형식 -> 400 응답")
    void signup_whenInvalidPhoneNumberFormat() throws Exception {
        // given
        SignupRequest request = RequestFactory.signupRequest(Value.defaults(), Value.defaults(),
                                                             Value.defaults(),
                                                             Value.of("010-1234-5678"));

        // when
        ResultActions result = mockMvc.perform(
                post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.success").value(false));
        result.andExpect(jsonPath("$.payload.code").value(METHOD_ARG_NOT_VALID.getCode()));
        result.andDo(print());
    }
}