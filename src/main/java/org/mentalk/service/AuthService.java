package org.mentalk.service;

import lombok.RequiredArgsConstructor;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public void checkEmailDuplicate(EmailDto emailDto) {
        memberRepository.findByEmail(emailDto.email())
                        .ifPresent(member -> {
                            throw new ApiException(ErrorCode.EMAIL_DUPLICATE);
                        });
    }

    @Transactional(readOnly = true)
    public void checkPhoneNumberDuplicate(PhoneNumberDto phoneNumberDto) {
        memberRepository.findByPhoneNumber(phoneNumberDto.phoneNumber())
                        .ifPresent(member -> {
                            throw new ApiException(ErrorCode.PHONE_NUMBER_DUPLICATE);
                        });
    }

    @Transactional(readOnly = true)
    public JwtDto login(LoginDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.email())
                                        .orElseThrow(
                                                () -> new ApiException(ErrorCode.INVALID_EMAIL));

        if (!passwordEncoder.matches(loginDto.password(), member.getPassword())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.createToken(member.getId(), member.getRole(), LoginProvider.OURS);
        return new JwtDto(token);
    }
}
