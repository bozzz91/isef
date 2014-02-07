package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.repo.PersonRepo;
import ru.desu.home.isef.services.PersonService;

@Service("personService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonRepo dao;

    @Override
    public Person find(String email) {
        return dao.findByEmail(email);
    }

    @Override
    public Person save(Person p) {
        return dao.saveAndFlush(p);
    }

    @Override
    public void delete(Person p) {
        dao.delete(p);
    }

    @Override
    public Person findById(long id) {
        return dao.findOne(id);
    }
}
