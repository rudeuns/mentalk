package org.mentalk.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.domain.Member;
import org.mentalk.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    /**
     * save Test
     */

    @Test
    @DisplayName("일반 회원 저장 성공 - Role.USER Member 객체 반환")
    void save_whenUserMember() {
        // given
        Member member = Member.builder()
                              .username("any")
                              .phoneNumber("any")
                              .build();

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("멘토 회원 저장 성공 - Role.MENTOR Member 객체 반환")
    void save_whenMentorMember() {
        // given
        Member member = Member.builder()
                              .username("any")
                              .phoneNumber("any")
                              .role(Role.MENTOR)
                              .build();

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getRole()).isEqualTo(Role.MENTOR);
    }

    @Test
    @DisplayName("등록된 이메일로 회원 저장 시도 - 예외 발생")
    void save_whenEmailDuplicated() {
        // given
        String email = "test@test.com";
        Member member1 = Member.builder()
                               .email(email)
                               .username("any1")
                               .phoneNumber("any1")
                               .build();
        Member member2 = Member.builder()
                               .email(email)
                               .username("any2")
                               .phoneNumber("any2")
                               .build();
        memberRepository.save(member1);

        // when & then
        assertThatThrownBy(() -> memberRepository.save(member2)).isInstanceOf(
                DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("등록된 전화번호로 회원 저장 시도 - 예외 발생")
    void save_whenPhoneNumberDuplicated() {
        // given
        String phoneNumber = "01012345678";
        Member member1 = Member.builder()
                               .email("any1")
                               .username("any1")
                               .phoneNumber(phoneNumber)
                               .build();
        Member member2 = Member.builder()
                               .email("any2")
                               .username("any2")
                               .phoneNumber(phoneNumber)
                               .build();
        memberRepository.save(member1);

        // when & then
        assertThatThrownBy(() -> memberRepository.save(member2)).isInstanceOf(
                DataIntegrityViolationException.class);
    }

    /**
     * findByEmail Test
     */

    @Test
    @DisplayName("이메일로 등록된 회원 조회 - Member 객체 반환")
    void findByEmail_whenEmailExists() {
        // given
        String email = "test@test.com";
        Member member = Member.builder()
                              .email(email)
                              .username("any")
                              .phoneNumber("any")
                              .build();
        memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findByEmail(email);

        // then
        assertThat(foundMember.isPresent()).isTrue();
        assertThat(foundMember.get()
                              .getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("이메일로 등록되지 않은 회원 조회 - null 반환")
    void findByEmail_whenEmailNotExists() {
        // given
        String email = "test@test.com";

        // when
        Optional<Member> foundMember = memberRepository.findByEmail(email);

        // then
        assertThat(foundMember.isPresent()).isFalse();
    }

    /**
     * findByPhoneNumber Test
     */

    @Test
    @DisplayName("전화번호로 등록된 회원 조회 - Member 객체 반환")
    void findByPhoneNumber_whenPhoneNumberExists() {
        // given
        String phoneNumber = "01012345678";
        Member member = Member.builder()
                              .username("any")
                              .phoneNumber(phoneNumber)
                              .build();
        memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findByPhoneNumber(phoneNumber);

        // then
        assertThat(foundMember.isPresent()).isTrue();
        assertThat(foundMember.get()
                              .getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    @DisplayName("전화번호로 등록되지 않은 회원 조회 - null 반환")
    void findByPhoneNumber_whenPhoneNumberNotExists() {
        // given
        String phoneNumber = "01012345678";

        // when
        Optional<Member> foundMember = memberRepository.findByPhoneNumber(phoneNumber);

        // then
        assertThat(foundMember.isPresent()).isFalse();
    }

}