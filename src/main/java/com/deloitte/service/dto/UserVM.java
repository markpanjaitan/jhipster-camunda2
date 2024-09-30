package com.deloitte.service.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.Map;
import java.util.Set;

public class UserVM {

    private String login;
    private String id; // Add user ID field
    private Set<String> authorities;
    private Map<String, Object> details;

    public UserVM(String login, String id, Set<String> authorities, Map<String, Object> details) {
        this.login = login;
        this.id = id; // Set the ID
        this.authorities = authorities;
        this.details = details;
    }

    public String getId() {
        return id; // Getter for user ID
    }

    public boolean isActivated() {
        return true;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public String getLogin() {
        return login;
    }

    @JsonAnyGetter
    public Map<String, Object> getDetails() {
        return details;
    }
}
