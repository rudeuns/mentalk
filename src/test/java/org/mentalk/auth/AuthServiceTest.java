package org.mentalk.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mentalk.auth.domain.LocalAccount;
import org.mentalk.auth.dto.JwtDto;
import org.mentalk.auth.dto.LocalLoginDto;
import org.mentalk.common.enums.ErrorCode;
import org.mentalk.common.enums.Role;
import org.mentalk.common.exception.ApiException;
import org.mentalk.common.security.JwtUtil;
import org.mentalk.utils.DtoFactory;
import org.mentalk.utils.EntityFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private LocalAccountRepository localAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("[이메일 중복 확인] 중복 아닌 경우 -> 예외 없음")
    void whenEmailNotInUse() {
        // given
        given(localAccountRepository.existsByEmail(anyString())).willReturn(false);

        // when & then
        assertThatCode(
                () -> authService.checkEmailInUse("user@test.com")).doesNotThrowAnyException();

        verify(localAccountRepository, times(1)).existsByEmail(anyString());
    }

    @Test
    @DisplayName("[이메일 중복 확인] 중복인 경우 -> 예외 발생")
    void whenEmailInUse() {
        // given
        given(localAccountRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.checkEmailInUse("user@test.com"))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_EMAIL_IN_USE);

        verify(localAccountRepository, times(1)).existsByEmail(anyString());
    }

    @Test
    @DisplayName("[로컬 로그인] 성공")
    void localLogin_whenSuccess() {
        // given
        LocalLoginDto loginDto = DtoFactory.localLoginDtoWithDefaults();

        LocalAccount localAccount = EntityFactory.localAccountWithDefaults();
        given(localAccountRepository.findByEmail(anyString())).willReturn(
                Optional.of(localAccount));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        String token = "token";
        given(jwtUtil.createToken(anyLong(), any(Role.class))).willReturn(token);

        // when
        JwtDto jwtDto = authService.localLogin(loginDto);

        // then
        assertThat(jwtDto.token()).isEqualTo(token);

        verify(localAccountRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtUtil, times(1)).createToken(anyLong(), any(Role.class));
    }

    @Test
    @DisplayName("[로컬 로그인] 이메일을 찾을 수 없는 경우 -> 예외 발생")
    void localLogin_whenEmailNotFound() {
        // given
        LocalLoginDto loginDto = DtoFactory.localLoginDtoWithDefaults();

        given(localAccountRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.localLogin(loginDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_NOT_FOUND);

        verify(localAccountRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).createToken(anyLong(), any(Role.class));
    }


    @Test
    @DisplayName("[로컬 로그인] 비밀번호가 일치하지 않는 경우 -> 예외 발생")
    void localLogin_whenInvalidPassword() {
        // given
        LocalLoginDto loginDto = DtoFactory.localLoginDtoWithDefaults();

        LocalAccount localAccount = EntityFactory.localAccountWithDefaults();
        given(localAccountRepository.findByEmail(anyString())).willReturn(
                Optional.of(localAccount));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.localLogin(loginDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);

        verify(localAccountRepository, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtUtil, never()).createToken(anyLong(), any(Role.class));
    }
}