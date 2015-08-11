package ru.desu.home.isef.repo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;

import java.util.List;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

    List<Payment> findByType(int type, Sort sort);
    
    List<Payment> findByTypeAndStatus(int type, int status, Sort sort);

    List<Payment> findByPayOwnerAndTypeAndStatus(Person p, int type, int status, Sort sort);
}
