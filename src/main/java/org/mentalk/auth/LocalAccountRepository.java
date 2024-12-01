package org.mentalk.auth;

import java.util.Optional;
import org.mentalk.auth.domain.LocalAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalAccountRepository extends JpaRepository<LocalAccount, Long> {

    boolean existsByMemberId(Long memberId);

    boolean existsByEmail(String email);

    Optional<LocalAccount> findByEmail(String email);
}
