package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonWallet;
import ru.desu.home.isef.entity.Role;
import ru.desu.home.isef.entity.Role.Roles;
import ru.desu.home.isef.repo.PersonRepo;
import ru.desu.home.isef.repo.PersonWalletRepo;
import ru.desu.home.isef.repo.RoleRepo;
import ru.desu.home.isef.services.PersonService;

@Service("personService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonRepo dao;
    @Autowired
    RoleRepo roleDao;
    @Autowired
    PersonWalletRepo walletRepo;

    @Override
    public Person find(String email) {
        return dao.findByEmail(email);
    }

    @Override
    public Person save(Person p) {
        return dao.save(p);
    }

    @Override
    public void delete(Person p) {
        dao.delete(p);
    }

    @Override
    public Person findById(long id) {
        return dao.findOne(id);
    }

    @Override
    public Role findRole(Roles r) {
        switch (r) {
            case USER:
                return roleDao.findByRoleName("USER");
            case ADMIN:
                return roleDao.findByRoleName("ADMIN");
            default:
                return roleDao.findByRoleName("ANONYMOUS");
        }
    }

    @Override
    public Person findByRefCode(String value) {
        return dao.findByReferalLink(value);
    }

    @Override
    public Person saveWithWallets(Person p, Iterable<PersonWallet> pw) {
        Person saved = dao.save(p);
        walletRepo.delete(pw);
        return saved;
    }
}
