package org.mentalk.session;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mentalk.session.domain.Session;
import org.mentalk.utils.EntityFactory;
import org.mentalk.utils.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    @DisplayName("[Session] NotNull 위반 -> 예외 발생")
    void whenNotNullViolation() {
        // given
        Session session = EntityFactory.session(Value.of(null), Value.of(null), Value.of(null),
                                                Value.of(null));

        // when & then
        assertThatThrownBy(() -> sessionRepository.save(session)).isInstanceOf(
                DataIntegrityViolationException.class);
    }
}