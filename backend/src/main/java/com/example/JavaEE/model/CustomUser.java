package com.example.JavaEE.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "customUser")
public class CustomUser {

    private String username;
    @JsonIgnore
    private String password;
    @Id
    private String id;


    private Set<String> roles = new HashSet<>();

    private Instant lastPasswordChange = Instant.now();


    public CustomUser() {
    }

    public CustomUser(String password, String username, Set<String> roles, Instant lastPasswordChange) {
        this.password = password;
        this.username = username;
        this.roles = roles;
        this.lastPasswordChange = lastPasswordChange;
    }

    public Instant getLastPasswordChange() {
        return lastPasswordChange;
    }

    public void setLastPasswordChange(Instant lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getRoles() { return roles; }

    public void setRoles(Set<String> roles) { this.roles = roles; }

    public boolean hasRole(String roleName) {
        return roles.contains(roleName);
    }

}
