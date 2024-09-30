package com.deloitte.service;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

    private String userId;
    private Collection<GrantedAuthority> authorities;

    public void setUserContext(String userId, Collection<GrantedAuthority> authorities) {
        this.userId = userId;
        this.authorities = authorities;
    }

    public String getUserId() {
        return userId;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void clear() {
        userId = null;
        authorities = null;
    }
}
