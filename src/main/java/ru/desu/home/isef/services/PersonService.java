package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonWallet;
import ru.desu.home.isef.entity.Role;
import ru.desu.home.isef.entity.Role.Roles;

public interface PersonService {

    public Person find(String email);

    public Person findById(long id);

    public Person save(Person p);

    public void delete(Person p);

    public Role findRole(Roles r);

    public Person findByRefCode(String value);
    
    public Person saveWithWallets(Person p, Iterable<PersonWallet> ps);
}
