package org.mentalk.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.config.SecurityConfig;
import org.mentalk.dto.request.MemberCreateRequest;
import org.mentalk.dto.service.MemberDto;
import org.mentalk.enums.ErrorCode;
import org.mentalk.exception.ApiException;
import org.mentalk.security.JwtUtil;
import org.mentalk.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = MemberController.class)
@Import(SecurityConfig.class)
@MockBean(JwtUtil.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private MemberCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new MemberCreateRequest("test@test.com",
                                                "testPassword",
                                                "testUser",
                                                "01012345678",
                                                null);
    }

    /**
     * /api/members Test
     */

    @Test
    @DisplayName("회원 저장 성공 - 200 응답")
    void createMember() throws Exception {
        // given
        doNothing().when(memberService)
                   .createMember(any(MemberDto.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
        );

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andDo(print());
    }

    @Test
    @DisplayName("회원 저장 중 데이터 무결성 위반 - 500(S002) 응답")
    void createMember_whenDataIntegrityViolation() throws Exception {
        // given
        doThrow(new ApiException(ErrorCode.DATA_INTEGRITY_VIOLATION))
                .when(memberService)
                .createMember(any(MemberDto.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
        );

        // then
        actions.andExpect(status().isBadRequest());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(
                jsonPath("$.payload.code").value(ErrorCode.DATA_INTEGRITY_VIOLATION.getCode()));
        actions.andDo(print());
    }

    @Test
    @DisplayName("회원 저장 중 예기치 않은 오류 발생 - 500(S001) 응답")
    void createMember_whenUnexpectedException() throws Exception {
        // given
        doThrow(new ApiException(ErrorCode.UNEXPECTED_ERROR))
                .when(memberService)
                .createMember(any(MemberDto.class));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
        );

        // then
        actions.andExpect(status().isInternalServerError());
        actions.andExpect(jsonPath("$.success").value(false));
        actions.andExpect(jsonPath("$.payload.code").value(ErrorCode.UNEXPECTED_ERROR.getCode()));
        actions.andDo(print());
    }
}