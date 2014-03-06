package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;

public interface PersonRepo extends JpaRepository<Person, Long> {

    public Person findByEmail(String email);

    public Person findByReferalLink(String value);

    public Person findByEmailAndActiveTrue(String value);
    
    @Query("select p from Payment p where p.payOwner=?1 and p.type=1 and p.orderDate = "
            + "(select max(p1.orderDate) from Payment p1 where p1.payOwner=?1 and p1.type=1)")
    public Payment findLastPayment(Person p);
}
