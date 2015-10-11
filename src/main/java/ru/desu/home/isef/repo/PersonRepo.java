package ru.desu.home.isef.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Role;

public interface PersonRepo extends JpaRepository<Person, Long> {

    Person findByEmail(String email);

    Person findByReferalLink(String value);
    
    @Query("select p from Payment p where p.payOwner=?1 and p.type=1 and p.orderDate = "
            + "(select max(p1.orderDate) from Payment p1 where p1.payOwner=?1 and p1.type=1)")
    Payment findLastPayment(Person p);

	Page<Person> findByRoleNot(Role role, Pageable pageable);
}
