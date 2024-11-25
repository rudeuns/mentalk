package org.mentalk.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.domain.Event;
import org.mentalk.domain.Member;
import org.mentalk.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member mentor;

    @BeforeEach
    void setUp() {
        mentor = Member.builder()
                       .username("-")
                       .phoneNumber("-")
                       .role(Role.MENTOR)
                       .build();
    }

    /**
     * Create Test
     */
    @Test
    @DisplayName("Event 생성 성공 테스트")
    void save_whenSuccess() {
        // given
        memberRepository.save(mentor);
        Event event = Event.builder()
                           .title("-")
                           .mentor(mentor)
                           .build();

        // when
        Event savedEvent = eventRepository.save(event);

        // then
        assertThat(savedEvent.getId()).isNotNull();
        assertThat(savedEvent.getMentor()
                             .getId()).isEqualTo(mentor.getId());
    }

    @Test
    @DisplayName("Event title NotNull 위반 예외 테스트")
    void save_whenNotNullViolation() {
        // given
        memberRepository.save(mentor);
        Event event = Event.builder()
                           .mentor(mentor)
                           .build();

        // when & then
        assertThatThrownBy(() -> eventRepository.save(event)).isInstanceOf(
                DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("존재하지 않는 Member 객체 참조 예외 테스트")
    void save_whenForeignKeyViolation() {
        // given
        Event event = Event.builder()
                           .title("-")
                           .mentor(mentor)
                           .build();

        // when & then
        assertThatThrownBy(() -> eventRepository.save(event)).isInstanceOf(
                InvalidDataAccessApiUsageException.class);
    }
}