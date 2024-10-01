package com.deloitte.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ASSIGNEE = "ROLE_ASSIGNEE";

    public static final String APPROVER = "ROLE_APPROVER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {}
}
