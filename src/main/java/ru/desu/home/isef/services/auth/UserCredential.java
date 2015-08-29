package ru.desu.home.isef.services.auth;

import lombok.Data;
import ru.desu.home.isef.entity.Person;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserCredential implements Serializable {
    public static final String USER_CREDENTIAL = "userCredential";
    
    private String account;
    private String name;
    private Person person;
	private String countryCode;
	private String countryName;
	private String ip;
    private Set<String> roles = new HashSet<>();

    public UserCredential(String account, String name, String role) {
        this.account = account;
        this.name = name;
		this.countryCode = "unknown";
		this.countryName = "unknown";
		roles.add(role);
    }

    public UserCredential() {
		this("anonymous", "anonymous", "anonymous");
    }

    public boolean isAnonymous() {
        return hasRole("anonymous") || "anonymous".equals(account);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
