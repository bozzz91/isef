package ru.desu.home.isef.services.auth;

public interface AuthenticationService {

    String login(String account, String password);

    void logout();

    UserCredential getUserCredential();
}
