package org.mentalk.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mentalk.domain.Event;
import org.springframework.stereotype.Repository;

@Repository
public class EventRepository {

    @PersistenceContext
    private EntityManager em;

    public Event save(Event event) {
        em.persist(event);
        return event;
    }
}
