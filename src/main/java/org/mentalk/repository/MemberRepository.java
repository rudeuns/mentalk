package org.mentalk.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import org.mentalk.domain.Member;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public Optional<Member> findByEmail(String email) {
        List<Member> result = em.createQuery("select m from Member m where m.email = :email",
                                             Member.class)
                                .setParameter("email", email)
                                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        List<Member> result = em.createQuery(
                                        "select m from Member m where m.phoneNumber = :phoneNumber", Member.class)
                                .setParameter("phoneNumber", phoneNumber)
                                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}
