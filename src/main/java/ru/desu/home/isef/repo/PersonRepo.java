package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Person;

public interface PersonRepo extends JpaRepository<Person, Long> {

    public Person findByEmail(String email);
    
}