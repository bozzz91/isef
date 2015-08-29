package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.Wallet;

import java.util.List;

public interface WalletService {
    
    List<Wallet> findAll();

    Wallet find(long id);

    Wallet save(Wallet p);

    void delete(Wallet p);
}
