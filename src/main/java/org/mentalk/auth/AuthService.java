package org.mentalk.auth;

import lombok.RequiredArgsConstructor;
import org.mentalk.auth.domain.LocalAccount;
import org.mentalk.auth.dto.JwtDto;
import org.mentalk.auth.dto.LocalLoginDto;
import org.mentalk.common.enums.ErrorCode;
import org.mentalk.common.exception.ApiException;
import org.mentalk.common.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LocalAccountRepository localAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public void checkEmailInUse(String email) {
        if (localAccountRepository.existsByEmail(email)) {
            throw new ApiException(ErrorCode.ALREADY_EMAIL_IN_USE);
        }
    }

    @Transactional(readOnly = true)
    public JwtDto localLogin(LocalLoginDto loginDto) {
        LocalAccount localAccount = localAccountRepository.findByEmail(loginDto.email())
                                                          .orElseThrow(() -> new ApiException(
                                                                  ErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(loginDto.password(), localAccount.getHashedPassword())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.createToken(localAccount.getMember().getId(),
                                           localAccount.getMember().getRole());
        return JwtDto.of(token);
    }
}
