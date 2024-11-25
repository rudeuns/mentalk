package org.mentalk.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.mentalk.enums.LoginProvider;
import org.mentalk.enums.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomSecurityContextFactory.class)
public @interface WithCustomMockMentor {

    long id() default 1;

    Role role() default Role.MENTOR;

    LoginProvider provider() default LoginProvider.OURS;
}
