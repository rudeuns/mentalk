package org.mentalk.annotation;

import java.lang.annotation.Annotation;
import org.mentalk.common.enums.Role;
import org.mentalk.common.security.PrincipalDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomSecurityContextFactory
        implements WithSecurityContextFactory<Annotation> {

    @Override
    public SecurityContext createSecurityContext(Annotation annotation) {
        long id = 1;
        Role role = Role.USER;

        if (annotation instanceof WithCustomMockUser customMockUser) {
            id = customMockUser.id();
            role = customMockUser.role();
        } else if (annotation instanceof WithCustomMockMentor customMockMentor) {
            id = customMockMentor.id();
            role = customMockMentor.role();
        }

        PrincipalDetails principal = new PrincipalDetails(id, role);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
                                                                                principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
