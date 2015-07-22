package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Ban;
import ru.desu.home.isef.repo.BanRepo;
import ru.desu.home.isef.services.BanService;

import java.util.List;

@Service("banService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BanServiceImpl implements BanService {

    @Autowired BanRepo dao;

    @Override
    public List<Ban> find(String url) {
        return dao.findByUrl(url);
    }

    @Override
    public Ban save(Ban b) {
        return dao.save(b);
    }

    @Override
    public List<Ban> findAll() {
        return dao.findAll();
    }
}
