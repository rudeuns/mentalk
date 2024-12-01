package org.mentalk.member;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mentalk.auth.LocalAccountService;
import org.mentalk.auth.dto.LocalAccountDto;
import org.mentalk.common.enums.ErrorCode;
import org.mentalk.common.exception.ApiException;
import org.mentalk.member.domain.Member;
import org.mentalk.member.dto.SignupDto;
import org.mentalk.utils.DtoFactory;
import org.mentalk.utils.EntityFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LocalAccountService localAccountService;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("[회원가입] 신규 회원 가입 성공")
    void signup_whenSuccessWithNewMember() {
        // given
        SignupDto signupDto = DtoFactory.signupDtoWithDefaults();

        given(memberRepository.findByPhoneNumber(anyString())).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(
                EntityFactory.memberWithDefaults());

        doNothing().when(localAccountService).createLocalAccount(any(LocalAccountDto.class));

        // when & then
        assertThatCode(() -> memberService.signup(signupDto)).doesNotThrowAnyException();

        verify(memberRepository, times(1)).findByPhoneNumber(anyString());
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(localAccountService, times(1)).createLocalAccount(any(LocalAccountDto.class));
    }

    @Test
    @DisplayName("[회원가입] 로컬 계정이 없는 기존 회원 가입 성공")
    void signup_whenSuccessWithNewLocalAccount() {
        // given
        SignupDto signupDto = DtoFactory.signupDtoWithDefaults();

        given(memberRepository.findByPhoneNumber(anyString())).willReturn(
                Optional.of(EntityFactory.memberWithDefaults()));

        doNothing().when(localAccountService).createLocalAccount(any(LocalAccountDto.class));

        // when & then
        assertThatCode(() -> memberService.signup(signupDto)).doesNotThrowAnyException();

        verify(memberRepository, times(1)).findByPhoneNumber(anyString());
        verify(memberRepository, never()).save(any(Member.class));
        verify(localAccountService, times(1)).createLocalAccount(any(LocalAccountDto.class));
    }

    @Test
    @DisplayName("[회원가입] 로컬 계정이 있는 기존 회원 -> 예외 발생")
    void signup_whenAlreadyRegistered() {
        // given
        SignupDto signupDto = DtoFactory.signupDtoWithDefaults();

        given(memberRepository.findByPhoneNumber(anyString())).willReturn(
                Optional.of(EntityFactory.memberWithDefaults()));

        doThrow(new ApiException(ErrorCode.ALREADY_REGISTERED))
                .when(localAccountService)
                .createLocalAccount(any(LocalAccountDto.class));

        // when & then
        assertThatThrownBy(() -> memberService.signup(signupDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_REGISTERED);

        verify(memberRepository, times(1)).findByPhoneNumber(anyString());
        verify(memberRepository, never()).save(any(Member.class));
        verify(localAccountService, times(1)).createLocalAccount(any(LocalAccountDto.class));
    }

    @Test
    @DisplayName("[회원가입] 이메일 중복 -> 예외 발생")
    void signup_whenEmailInUse() {
        // given
        SignupDto signupDto = DtoFactory.signupDtoWithDefaults();

        given(memberRepository.findByPhoneNumber(anyString())).willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class))).willReturn(
                EntityFactory.memberWithDefaults());

        doThrow(new ApiException(ErrorCode.ALREADY_EMAIL_IN_USE))
                .when(localAccountService)
                .createLocalAccount(any(LocalAccountDto.class));

        // when & then
        assertThatThrownBy(() -> memberService.signup(signupDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_EMAIL_IN_USE);

        verify(memberRepository, times(1)).findByPhoneNumber(anyString());
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(localAccountService, times(1)).createLocalAccount(any(LocalAccountDto.class));
    }
}