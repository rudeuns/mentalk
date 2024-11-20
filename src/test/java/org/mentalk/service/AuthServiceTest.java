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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mentalk.domain.Member;
import org.mentalk.dto.AuthRequest;
import org.mentalk.dto.AuthRequest.Email;
import org.mentalk.dto.AuthRequest.Login;
import org.mentalk.dto.AuthRequest.PhoneNumber;
import org.mentalk.dto.JwtDto;
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

    /**
     * validateEmailDuplicate Test
     */

    @Test
    @DisplayName("이메일 중복 아닌 경우 - 예외 없음")
    void validateEmail_whenNotDuplicated() {
        // given
        String email = "test@test.com";
        AuthRequest.Email emailDto = new Email(email);

        given(memberRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatCode(() -> authService.validateEmailDuplicate(emailDto)).doesNotThrowAnyException();

        // verify
        verify(memberRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("이메일 중복인 경우 - 예외 발생")
    void validateEmail_whenDuplicated() {
        // given
        String email = "test@test.com";
        Member member = mock(Member.class);
        AuthRequest.Email emailDto = new Email(email);

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> authService.validateEmailDuplicate(emailDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_DUPLICATED);

        // verify
        verify(memberRepository, times(1)).findByEmail(email);
    }

    /**
     * validatePhoneNumberDuplicate Test
     */

    @Test
    @DisplayName("전화번호 중복 아닌 경우 - 예외 없음")
    void validatePhoneNumber_whenNotDuplicated() {
        // given
        String phoneNumber = "01012345678";
        AuthRequest.PhoneNumber phoneNumberDto = new PhoneNumber(phoneNumber);

        given(memberRepository.findByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        //when & then
        assertThatCode(() -> authService.validatePhoneNumberDuplicate(phoneNumberDto)).doesNotThrowAnyException();

        // verify
        verify(memberRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    @Test
    @DisplayName("전화번호 중복인 경우 - 예외 발생")
    void validatePhoneNumber_whenDuplicated() {
        // given
        String phoneNumber = "01012345678";
        Member member = mock(Member.class);
        AuthRequest.PhoneNumber phoneNumberDto = new PhoneNumber(phoneNumber);

        given(memberRepository.findByPhoneNumber(phoneNumber)).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(
                () -> authService.validatePhoneNumberDuplicate(phoneNumberDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PHONE_NUMBER_DUPLICATED);

        // verify
        verify(memberRepository, times(1)).findByPhoneNumber(phoneNumber);
    }

    /**
     * login Test
     */

    @Test
    @DisplayName("로그인 성공 - JwtDto 객체 반환")
    void login_whenSuccess() {
        // given
        String email = "test@test.com";
        String password = "testPassword";
        Member member = mock(Member.class);
        String jwtToken = "jwtToken";
        AuthRequest.Login loginDto = new Login(email, password);

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(password, member.getPassword())).willReturn(true);
        given(jwtUtil.createToken(member.getId(), member.getRole(), LoginProvider.OURS)).willReturn(jwtToken);

        // when
        JwtDto jwtDto = authService.login(loginDto);

        // then
        assertThat(jwtDto).isNotNull();
        assertThat(jwtDto.token()).isEqualTo(jwtToken);

        // verify
        verify(memberRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, member.getPassword());
        verify(jwtUtil, times(1)).createToken(member.getId(), member.getRole(), LoginProvider.OURS);
    }

    @Test
    @DisplayName("로그인 이메일 잘못 입력 - 예외 발생")
    void login_whenInvalidEmail() {
        // given
        String email = "test@test.com";
        String password = "testPassword";
        AuthRequest.Login loginDto = new Login(email, password);

        given(memberRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_EMAIL);

        // verify
        verify(memberRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).createToken(any(), any(), any());
    }

    @Test
    @DisplayName("로그인 비밀번호 잘못 입력 - 예외 발생")
    void login_whenInvalidPassword() {
        // given
        String email = "test@test.com";
        String password = "testPassword";
        Member member = mock(Member.class);
        AuthRequest.Login loginDto = new Login(email, password);

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(password, member.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);

        // verify
        verify(memberRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, member.getPassword());
        verify(jwtUtil, never()).createToken(any(), any(), any());
    }
}