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
import org.mentalk.dto.service.MemberDto;
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

    private String encodedPassword;
    private MemberDto memberDto;

    @BeforeEach
    void setUp() {
        encodedPassword = "encodedPassword";
        memberDto = new MemberDto("test@test.com",
                                  "testPassword",
                                  "testUser",
                                  "01012345678",
                                  null);
    }

    /**
     * createMember Test
     */

    @Test
    @DisplayName("회원 저장 성공 - 예외 없음")
    void createMember() {
        // given
        Member member = mock(Member.class);

        given(passwordEncoder.encode(any(String.class))).willReturn(encodedPassword);
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // when & then
        assertThatCode(() -> memberService.createMember(memberDto)).doesNotThrowAnyException();

        // verify
        verify(passwordEncoder, times(1)).encode(any(String.class));
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 저장 중 데이터 무결성 위반 - 예외 발생")
    void createMember_whenDataIntegrityViolation() {
        // given
        given(passwordEncoder.encode(any(String.class))).willReturn(encodedPassword);
        given(memberRepository.save(any(Member.class))).willThrow(
                DataIntegrityViolationException.class);

        // when & then
        assertThatThrownBy(() -> memberService.createMember(memberDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DATA_INTEGRITY_VIOLATION);

        // verify
        verify(passwordEncoder, times(1)).encode(any(String.class));
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 저장 중 예기치 않은 오류 - 예외 발생")
    void createMember_whenUnexpectedException() {
        // given
        given(passwordEncoder.encode(any(String.class))).willReturn(encodedPassword);
        given(memberRepository.save(any(Member.class))).willThrow(RuntimeException.class);

        // when & then
        assertThatThrownBy(() -> memberService.createMember(memberDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNEXPECTED_ERROR);

        // verify
        verify(passwordEncoder, times(1)).encode(any(String.class));
        verify(memberRepository, times(1)).save(any(Member.class));
    }
}