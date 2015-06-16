package ru.desu.home.isef.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Wallet;
import ru.desu.home.isef.repo.WalletRepo;
import ru.desu.home.isef.services.WalletService;

@Service("walletService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WalletServiceImpl implements WalletService {

    @Autowired WalletRepo dao;

    @Override
    public Wallet find(long id) {
        return dao.findOne(id);
    }

    @Override
    public Wallet save(Wallet p) {
        return dao.save(p);
    }

    @Override
    public void delete(Wallet p) {
        dao.delete(p);
    }

    @Override
    public List<Wallet> findAll() {
        return dao.findAll();
    }
}
