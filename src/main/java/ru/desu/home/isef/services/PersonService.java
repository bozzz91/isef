package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.*;
import ru.desu.home.isef.entity.Role.Roles;

import java.util.List;

public interface PersonService {

    Person find(String email);
    
    Person findAdmin();

    Person findById(long id);

    Person save(Person p);

    void delete(Person p);

    Role findRole(Roles r);

    Person findByRefCode(String value);
    
    Person saveWithWallets(Person p, Iterable<PersonWallet> ps);
    
    Payment getLastPayment(Person p);

    List<Person> findAll();
    
    Rating getRating(Person p);
    
    Rating getNextRating(Rating current);
    
    List<Person> findTop(int count);
}
