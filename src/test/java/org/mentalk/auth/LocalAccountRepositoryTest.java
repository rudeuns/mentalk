package org.mentalk.auth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.auth.domain.LocalAccount;
import org.mentalk.member.MemberRepository;
import org.mentalk.member.domain.Member;
import org.mentalk.utils.EntityFactory;
import org.mentalk.utils.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class LocalAccountRepositoryTest {

    @Autowired
    private LocalAccountRepository localAccountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("[LocalAccount] NotNull 위반 -> 예외 발생")
    void whenNotNullViolation() {
        // given
        LocalAccount localAccount = EntityFactory.localAccount(Value.of(null), Value.of(null),
                                                               Value.of(null));

        // when & then
        assertThatThrownBy(() -> localAccountRepository.save(localAccount)).isInstanceOf(
                DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("[LocalAccount] member 중복 -> 예외 발생")
    void whenMemberUniqueViolation() {
        // given
        Member member = EntityFactory.member(Value.defaults(), Value.defaults(), Value.defaults());
        memberRepository.save(member);

        LocalAccount account1 = EntityFactory.localAccount(Value.of(member),
                                                           Value.of("user1@mentalk.com"),
                                                           Value.defaults());
        LocalAccount account2 = EntityFactory.localAccount(Value.of(member),
                                                           Value.of("user2@mentalk.com"),
                                                           Value.defaults());

        localAccountRepository.save(account1);

        // when & then
        assertThatThrownBy(() -> localAccountRepository.save(account2)).isInstanceOf(
                DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("[LocalAccount] email 중복 -> 예외 발생")
    void whenEmailUniqueViolation() {
        // given
        Member member1 = EntityFactory.member(Value.of("user1"), Value.of("01012345678"),
                                              Value.defaults());
        Member member2 = EntityFactory.member(Value.of("user2"), Value.of("01087654321"),
                                              Value.defaults());
        memberRepository.save(member1);
        memberRepository.save(member2);

        LocalAccount account1 = EntityFactory.localAccount(Value.of(member1),
                                                           Value.of("user@mentalk.com"),
                                                           Value.defaults());
        LocalAccount account2 = EntityFactory.localAccount(Value.of(member2),
                                                           Value.of("user@mentalk.com"),
                                                           Value.defaults());

        localAccountRepository.save(account1);

        // when & then
        assertThatThrownBy(() -> localAccountRepository.save(account2)).isInstanceOf(
                DataIntegrityViolationException.class);
    }
}