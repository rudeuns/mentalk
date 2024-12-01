package org.mentalk.auth;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mentalk.auth.domain.LocalAccount;
import org.mentalk.auth.dto.LocalAccountDto;
import org.mentalk.common.enums.ErrorCode;
import org.mentalk.common.exception.ApiException;
import org.mentalk.utils.DtoFactory;
import org.mentalk.utils.EntityFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class LocalAccountServiceTest {

    @Mock
    private LocalAccountRepository localAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LocalAccountService localAccountService;

    @Test
    @DisplayName("[로컬 계정 생성] 신규 회원 계정 생성 성공")
    void createLocalAccount_whenSuccess() {
        // given
        LocalAccountDto localAccountDto = DtoFactory.localAccountDtoWithDefaults();

        given(localAccountRepository.existsByMemberId(anyLong())).willReturn(false);
        given(localAccountRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("hashedPassword");

        LocalAccount localAccount = EntityFactory.localAccountWithDefaults();
        given(localAccountRepository.save(any(LocalAccount.class))).willReturn(localAccount);

        // when & then
        assertThatCode(() -> localAccountService.createLocalAccount(
                localAccountDto)).doesNotThrowAnyException();

        verify(localAccountRepository, times(1)).existsByMemberId(anyLong());
        verify(localAccountRepository, times(1)).existsByEmail(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(localAccountRepository, times(1)).save(any(LocalAccount.class));
    }

    @Test
    @DisplayName("[로컬 계정 생성] 기존 회원 -> 예외 발생")
    void createLocalAccount_whenAlreadyRegistered() {
        // given
        LocalAccountDto localAccountDto = DtoFactory.localAccountDtoWithDefaults();

        given(localAccountRepository.existsByMemberId(anyLong())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> localAccountService.createLocalAccount(localAccountDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_REGISTERED);

        verify(localAccountRepository, times(1)).existsByMemberId(anyLong());
        verify(localAccountRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(localAccountRepository, never()).save(any(LocalAccount.class));
    }

    @Test
    @DisplayName("[로컬 계정 생성] 사용 중인 이메일 -> 예외 발생")
    void createLocalAccount_whenEmailInUse() {
        // given
        LocalAccountDto localAccountDto = DtoFactory.localAccountDtoWithDefaults();

        given(localAccountRepository.existsByMemberId(anyLong())).willReturn(false);
        given(localAccountRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> localAccountService.createLocalAccount(localAccountDto))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_EMAIL_IN_USE);

        verify(localAccountRepository, times(1)).existsByMemberId(anyLong());
        verify(localAccountRepository, times(1)).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(localAccountRepository, never()).save(any(LocalAccount.class));
    }
}