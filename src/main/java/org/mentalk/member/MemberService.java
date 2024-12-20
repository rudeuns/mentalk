package org.mentalk.member;

import lombok.RequiredArgsConstructor;
import org.mentalk.auth.LocalAccountService;
import org.mentalk.auth.dto.JwtDto;
import org.mentalk.auth.dto.LocalAccountDto;
import org.mentalk.common.enums.ErrorCode;
import org.mentalk.common.enums.Role;
import org.mentalk.common.exception.ApiException;
import org.mentalk.common.security.JwtUtil;
import org.mentalk.member.domain.Member;
import org.mentalk.member.dto.MemberDto;
import org.mentalk.member.dto.SignupDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LocalAccountService localAccountService;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signup(SignupDto signupDto) {
        Member member = memberRepository.findByPhoneNumber(signupDto.phoneNumber())
                                        .orElseGet(() -> {
                                            MemberDto memberDto = MemberDto.of(signupDto.name(),
                                                                               signupDto.phoneNumber());
                                            return memberRepository.save(memberDto.toEntity());
                                        });

        LocalAccountDto localAccountDto = LocalAccountDto.of(member, signupDto.email(),
                                                             signupDto.password());
        localAccountService.createLocalAccount(localAccountDto);
    }

    @Transactional
    public JwtDto changeRoleToMentor(Long memberId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
        member.changeRole(Role.MENTOR);
        memberRepository.save(member);

        String token = jwtUtil.createToken(member.getId(), member.getRole());

        return JwtDto.of(token, member.getRole());
    }
}
