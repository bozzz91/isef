package ru.desu.home.isef.services;

import java.util.List;
import ru.desu.home.isef.entity.Wallet;

public interface WalletService {
    
    public List<Wallet> findAll();

    public Wallet find(long id);

    public Wallet save(Wallet p);

    public void delete(Wallet p);
}
