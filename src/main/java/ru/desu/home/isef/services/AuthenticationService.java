package ru.desu.home.isef.services;

public interface AuthenticationService {

    public boolean login(String account, String password);

    public void logout();

    public UserCredential getUserCredential();

}
