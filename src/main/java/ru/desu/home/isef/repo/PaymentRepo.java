package ru.desu.home.isef.repo;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

    public List<Payment> findByType(int type, Sort sort);
    
    public List<Payment> findByTypeAndStatus(int type, int status, Sort sort);

    public List<Payment> findByPayOwnerAndTypeAndStatus(Person p, int type, int status, Sort sort);
}
