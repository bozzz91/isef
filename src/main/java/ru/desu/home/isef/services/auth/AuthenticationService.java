package ru.desu.home.isef.services.auth;

public interface AuthenticationService {

    public String login(String account, String password);

    public void logout();

    public UserCredential getUserCredential();

}
