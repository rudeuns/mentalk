package org.mentalk.auth;

import lombok.RequiredArgsConstructor;
import org.mentalk.auth.domain.LocalAccount;
import org.mentalk.auth.dto.EmailDto;
import org.mentalk.auth.dto.JwtDto;
import org.mentalk.auth.dto.LocalLoginDto;
import org.mentalk.common.enums.ErrorCode;
import org.mentalk.common.exception.ApiException;
import org.mentalk.common.security.JwtUtil;
import org.mentalk.member.MemberRepository;
import org.mentalk.member.domain.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LocalAccountRepository localAccountRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return localAccountRepository.existsByEmail(email);
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

        return JwtDto.of(token, localAccount.getMember().getRole());
    }

    @Transactional(readOnly = true)
    public EmailDto findEmail(String phoneNumber) {
        Member member = memberRepository.findByPhoneNumber(phoneNumber)
                                        .orElseThrow(
                                                () -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        LocalAccount localAccount = localAccountRepository.findByMemberId(member.getId())
                                                          .orElseThrow(() -> new ApiException(
                                                                  ErrorCode.ACCOUNT_NOT_FOUND));

        return EmailDto.of(localAccount.getEmail());
    }

    @Transactional
    public void resetPassword(String email, String password) {
        LocalAccount localAccount = localAccountRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(ErrorCode.ACCOUNT_NOT_FOUND));

        String hashedPassword = passwordEncoder.encode(password);
        localAccount.changePassword(hashedPassword);

        localAccountRepository.save(localAccount);
    }
}
