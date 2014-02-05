package ru.desu.home.isef.services;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class UserCredential implements Serializable {
    String account;
    String name;
    Set<String> roles = new HashSet<>();

    public UserCredential(String account, String name) {
        this.account = account;
        this.name = name;
    }

    public UserCredential() {
        this.account = "anonymous";
        this.name = "Anonymous";
        roles.add("anonymous");
    }

    public boolean isAnonymous() {
        return hasRole("anonymous") || "anonymous".equals(account);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public void addRole(String role) {
        roles.add(role);
    }

}
