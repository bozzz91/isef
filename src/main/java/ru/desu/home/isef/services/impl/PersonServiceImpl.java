package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PersonService;

@Service("PersonService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonDao dao;

    @Override
    @Transactional
    public Person add(Person p) {
        return dao.save(p);
    }

    @Override
    @Transactional
    public Person find(String email) {
        return dao.get(email);
    }

    @Override
    public Person update(Person p) {
        return dao.update(p);
    }

    @Override
    public void delete(Person p) {
        dao.delete(p);
    }

}
