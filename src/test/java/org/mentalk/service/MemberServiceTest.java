package org.mentalk.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mentalk.domain.Member;
import org.mentalk.dto.MemberRequest;
import org.mentalk.enums.ErrorCode;
import org.mentalk.exception.ApiException;
import org.mentalk.repository.MemberRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private String password;
    private String encodedPassword;
    private MemberRequest.Register registerDto;

    @BeforeEach
    void setUp() {
        password = "password";
        encodedPassword = "encodedPassword";
        registerDto = MemberRequest.Register.builder()
                                            .email("test@test.com")
                                            .password(password)
                                            .username("testUser")
                                            .phoneNumber("01012345678")
                                            .build();
    }

    /**
     * registerMember Test
     */

    @Test
    @DisplayName("회원 등록 성공 - 예외 없음")
    void registerMember() {
        // given
        Member member = mock(Member.class);

        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // when & then
        assertThatCode(() -> memberService.registerMember(registerDto)).doesNotThrowAnyException();

        // verify
        verify(passwordEncoder, times(1)).encode(password);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 등록 중 데이터 무결성 위반 - 예외 발생")
    void registerMember_whenDataIntegrityViolation() {
        // given
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(memberRepository.save(any(Member.class))).willThrow(DataIntegrityViolationException.class);

        // when & then
        assertThatThrownBy(() -> memberService.registerMember(registerDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REGISTER_DATA_INTEGRITY);

        // verify
        verify(passwordEncoder, times(1)).encode(password);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 등록 중 예기치 않은 오류 - 예외 발생")
    void registerMember_whenUnexpectedException() {
        // given
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(memberRepository.save(any(Member.class))).willThrow(RuntimeException.class);

        // when & then
        assertThatThrownBy(() -> memberService.registerMember(registerDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNEXPECTED_ERROR);
        
        // verify
        verify(passwordEncoder, times(1)).encode(password);
        verify(memberRepository, times(1)).save(any(Member.class));
    }
}