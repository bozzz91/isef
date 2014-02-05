package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.Person;

public interface PersonService {

    public Person find(String email);

    public Person save(Person p);
    
    public void delete(Person p);
}
