package ru.desu.home.isef.services.auth;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import ru.desu.home.isef.entity.Person;

@Data
public class UserCredential implements Serializable {

    String account;
    String name;
    Person person;
    Set<String> roles = new HashSet<>();

    public UserCredential(String account, String name) {
        this.account = account;
        this.name = name;
    }

    public UserCredential() {
        this.account = "Anonymous";
        this.name = "Anonymous";
        roles.add("Anonymous");
    }

    public boolean isAnonymous() {
        return hasRole("Anonymous") || "Anonymous".equals(account);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public void addRole(String role) {
        roles.add(role);
    }

}
