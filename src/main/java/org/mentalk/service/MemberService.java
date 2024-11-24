package org.mentalk.service;

import lombok.RequiredArgsConstructor;
import org.mentalk.domain.Member;
import org.mentalk.dto.service.MemberDto;
import org.mentalk.enums.ErrorCode;
import org.mentalk.exception.ApiException;
import org.mentalk.repository.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createMember(MemberDto memberDto) {
        try {
            String encodePassword = passwordEncoder.encode(memberDto.password());
            Member member = memberDto.toEntity(encodePassword);
            memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.DATA_INTEGRITY_VIOLATION);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.UNEXPECTED_ERROR);
        }
    }
}
