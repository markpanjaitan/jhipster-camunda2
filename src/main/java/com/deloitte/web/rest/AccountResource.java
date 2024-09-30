package com.deloitte.web.rest;

import com.deloitte.security.SecurityUtils;
import com.deloitte.service.dto.UserVM;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountResource {

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

    private static class AccountResourceException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private AccountResourceException(String message) {
            super(message);
        }
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @param principal the current user; resolves to {@code null} if not authenticated.
     * @return the current user.
     * @throws AccountResourceException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public UserVM getAccount(Principal principal) {
        if (principal instanceof AbstractAuthenticationToken) {
            AbstractAuthenticationToken auth = (AbstractAuthenticationToken) principal;
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

            // Extract roles from authorities
            Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

            roles.forEach(role -> System.out.println(role)); // Print roles for debugging

            return getUserFromAuthentication(auth, roles);
        } else {
            throw new AccountResourceException("User could not be found");
        }
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param principal the authentication principal.
     * @return the login if the user is authenticated.
     */
    @GetMapping(value = "/authenticate", produces = MediaType.TEXT_PLAIN_VALUE)
    public String isAuthenticated(Principal principal) {
        LOG.debug("REST request to check if the current user is authenticated");
        return principal == null ? null : principal.getName();
    }

    public static UserVM getUserFromAuthentication(AbstractAuthenticationToken authToken, Set<String> roles) {
        Map<String, Object> attributes;
        String userId = null; // Variable to hold user ID

        if (authToken instanceof JwtAuthenticationToken) {
            attributes = ((JwtAuthenticationToken) authToken).getTokenAttributes();
            userId = (String) attributes.get("sub"); // Adjust based on your JWT claims
        } else if (authToken instanceof OAuth2AuthenticationToken) {
            attributes = ((OAuth2AuthenticationToken) authToken).getPrincipal().getAttributes();
            userId = (String) attributes.get("preferred_username"); // Adjust based on your OAuth claims
        } else {
            throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT!");
        }

        return new UserVM(
            authToken.getName(),
            userId, // Pass user ID to UserVM
            roles, // Pass roles to UserVM
            SecurityUtils.extractDetailsFromTokenAttributes(attributes)
        );
    }
}
