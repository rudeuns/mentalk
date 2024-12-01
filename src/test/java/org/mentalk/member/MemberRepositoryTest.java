package org.mentalk.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.common.enums.Role;
import org.mentalk.member.domain.Member;
import org.mentalk.utils.EntityFactory;
import org.mentalk.utils.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("[Member] NotNull 위반 -> 예외 발생")
    void whenNotNullViolation() {
        // given
        Member member = EntityFactory.member(Value.of(null), Value.of(null), Value.of(null));

        // when & then
        assertThatThrownBy(() -> memberRepository.save(member)).isInstanceOf(
                DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("[Member] phoneNumber 중복 저장 -> 예외 발생")
    void whenPhoneNumberUniqueViolation() {
        // given
        Member member1 = EntityFactory.member(Value.defaults(), Value.of("01012345678"),
                                              Value.defaults());
        Member member2 = EntityFactory.member(Value.defaults(), Value.of("01012345678"),
                                              Value.defaults());

        memberRepository.save(member1);

        // when & then
        assertThatThrownBy(() -> memberRepository.save(member2)).isInstanceOf(
                DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("[Member] role이 null인 경우 -> Role.User로 저장")
    void whenRoleIsNull() {
        // given
        Member member = EntityFactory.member(Value.defaults(), Value.defaults(), Value.of(null));

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember.getRole()).isEqualTo(Role.USER);
    }
}