package org.mentalk.member;

import lombok.RequiredArgsConstructor;
import org.mentalk.auth.LocalAccountService;
import org.mentalk.auth.dto.LocalAccountDto;
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
}
