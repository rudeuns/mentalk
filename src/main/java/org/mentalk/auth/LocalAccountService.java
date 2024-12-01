package org.mentalk.auth;

import lombok.RequiredArgsConstructor;
import org.mentalk.auth.dto.LocalAccountDto;
import org.mentalk.common.enums.ErrorCode;
import org.mentalk.common.exception.ApiException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocalAccountService {

    private final LocalAccountRepository localAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createLocalAccount(LocalAccountDto localAccountDto) {
        if (localAccountRepository.existsByMemberId(localAccountDto.member().getId())) {
            throw new ApiException(ErrorCode.ALREADY_REGISTERED);
        }

        if (localAccountRepository.existsByEmail(localAccountDto.email())) {
            throw new ApiException(ErrorCode.ALREADY_EMAIL_IN_USE);
        }

        String hashedPassword = passwordEncoder.encode(localAccountDto.password());

        localAccountRepository.save(localAccountDto.toEntity(hashedPassword));
    }
}
