package org.mentalk.annotation;

import java.lang.annotation.Annotation;
import org.mentalk.enums.LoginProvider;
import org.mentalk.enums.Role;
import org.mentalk.security.PrincipalDetails;
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
        LoginProvider provider = LoginProvider.OURS;

        if (annotation instanceof WithCustomMockUser customMockUser) {
            id = customMockUser.id();
            role = customMockUser.role();
            provider = customMockUser.provider();
        } else if (annotation instanceof WithCustomMockMentor customMockMentor) {
            id = customMockMentor.id();
            role = customMockMentor.role();
            provider = customMockMentor.provider();
        }

        PrincipalDetails principal = new PrincipalDetails(id, role, provider);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
                                                                                principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
