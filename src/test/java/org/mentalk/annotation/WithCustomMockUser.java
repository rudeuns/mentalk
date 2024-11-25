package org.mentalk.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.mentalk.enums.LoginProvider;
import org.mentalk.enums.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomSecurityContextFactory.class)
public @interface WithCustomMockUser {

    long id() default 1;

    Role role() default Role.USER;

    LoginProvider provider() default LoginProvider.OURS;
}
