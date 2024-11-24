package org.mentalk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mentalk.domain.Member;
import org.mentalk.dto.service.EmailDto;
import org.mentalk.dto.service.JwtDto;
import org.mentalk.dto.service.LoginDto;
import org.mentalk.dto.service.PhoneNumberDto;
import org.mentalk.enums.ErrorCode;
import org.mentalk.enums.LoginProvider;
import org.mentalk.exception.ApiException;
import org.mentalk.repository.MemberRepository;
import org.mentalk.security.JwtUtil;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private EmailDto emailDto;
    private PhoneNumberDto phoneNumberDto;
    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        emailDto = new EmailDto("test@test.com");
        phoneNumberDto = new PhoneNumberDto("01012345678");
        loginDto = new LoginDto("test@test.com", "testPassword");
    }

    /**
     * checkEmailDuplicate Test
     */

    @Test
    @DisplayName("이메일 중복 아닌 경우 - 예외 없음")
    void checkEmail_whenNotDuplicate() {
        // given
        given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.empty());

        // when & then
        assertThatCode(
                () -> authService.checkEmailDuplicate(emailDto)).doesNotThrowAnyException();

        // verify
        verify(memberRepository, times(1)).findByEmail(any(String.class));
    }

    @Test
    @DisplayName("이메일 중복인 경우 - 예외 발생")
    void checkEmail_whenDuplicate() {
        // given
        Member member = mock(Member.class);
        given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> authService.checkEmailDuplicate(emailDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_DUPLICATE);

        // verify
        verify(memberRepository, times(1)).findByEmail(any(String.class));
    }

    /**
     * checkPhoneNumberDuplicate Test
     */

    @Test
    @DisplayName("전화번호 중복 아닌 경우 - 예외 없음")
    void checkPhoneNumber_whenNotDuplicate() {
        // given
        given(memberRepository.findByPhoneNumber(any(String.class))).willReturn(Optional.empty());

        //when & then
        assertThatCode(() -> authService.checkPhoneNumberDuplicate(
                phoneNumberDto)).doesNotThrowAnyException();

        // verify
        verify(memberRepository, times(1)).findByPhoneNumber(any(String.class));
    }

    @Test
    @DisplayName("전화번호 중복인 경우 - 예외 발생")
    void checkPhoneNumber_whenDuplicate() {
        // given
        Member member = mock(Member.class);
        given(memberRepository.findByPhoneNumber(any(String.class))).willReturn(
                Optional.of(member));

        // when & then
        assertThatThrownBy(
                () -> authService.checkPhoneNumberDuplicate(phoneNumberDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PHONE_NUMBER_DUPLICATE);

        // verify
        verify(memberRepository, times(1)).findByPhoneNumber(any(String.class));
    }

    /**
     * login Test
     */

    @Test
    @DisplayName("로그인 성공 - JwtDto 객체 반환")
    void login_whenSuccess() {
        // given
        Member member = mock(Member.class);
        String jwtToken = "jwtToken";

        given(memberRepository.findByEmail(loginDto.email())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(loginDto.password(), member.getPassword())).willReturn(true);
        given(jwtUtil.createToken(member.getId(), member.getRole(), LoginProvider.OURS)).willReturn(
                jwtToken);

        // when
        JwtDto jwtDto = authService.login(loginDto);

        // then
        assertThat(jwtDto).isNotNull();
        assertThat(jwtDto.token()).isEqualTo(jwtToken);

        // verify
        verify(memberRepository, times(1)).findByEmail(loginDto.email());
        verify(passwordEncoder, times(1)).matches(loginDto.password(), member.getPassword());
        verify(jwtUtil, times(1)).createToken(member.getId(), member.getRole(), LoginProvider.OURS);
    }

    @Test
    @DisplayName("로그인 이메일 잘못 입력 - 예외 발생")
    void login_whenInvalidEmail() {
        // given
        given(memberRepository.findByEmail(loginDto.email())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_EMAIL);

        // verify
        verify(memberRepository, times(1)).findByEmail(loginDto.email());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).createToken(any(), any(), any());
    }

    @Test
    @DisplayName("로그인 비밀번호 잘못 입력 - 예외 발생")
    void login_whenInvalidPassword() {
        // given
        Member member = mock(Member.class);
        given(memberRepository.findByEmail(loginDto.email())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(loginDto.password(), member.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);

        // verify
        verify(memberRepository, times(1)).findByEmail(loginDto.email());
        verify(passwordEncoder, times(1)).matches(loginDto.password(), member.getPassword());
        verify(jwtUtil, never()).createToken(any(), any(), any());
    }
}