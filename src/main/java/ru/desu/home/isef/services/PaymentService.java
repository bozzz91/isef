package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;

import java.util.List;

public interface PaymentService {

    Payment findOne(Long id);
    
    Payment save(Payment p);
    
    void delete(Payment p);
    
    List<Payment> findAll();
    
    List<Payment> findRepayments();
    
    List<Payment> findRepayments(int type);
    
    List<Payment> findRepayments(int type, int state);

	List<Payment> findRepayments(Person p, int type, int state);

	List<Payment> findRepayments(Person p);
    
    Double getCurrency();
}
