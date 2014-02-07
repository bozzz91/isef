package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.ActivationPerson;
import ru.desu.home.isef.repo.ActivationPersonRepo;
import ru.desu.home.isef.services.ActivationPersonService;

@Service("activationService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ActivationPersonServiceImpl implements ActivationPersonService {

    @Autowired
    ActivationPersonRepo dao;

    @Override
    public ActivationPerson findByCode(String code) {
        return dao.findByCode(code);
    }

    @Override
    public ActivationPerson findById(long id) {
        return dao.findOne(id);
    }

    @Override
    public ActivationPerson save(ActivationPerson p) {
        return dao.save(p);
    }

    @Override
    public void delete(ActivationPerson p) {
        dao.delete(p);
    }

    @Override
    public ActivationPerson findByEmail(String email) {
        return dao.findByEmail(email);
    }
}
