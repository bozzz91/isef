package ru.desu.home.isef.services;

import java.util.List;
import ru.desu.home.isef.entity.Currency;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;

public interface PaymentService {

    public Payment findOne(Long id);
    
    public Payment save(Payment p);
    
    public void delete(Payment p);
    
    public List<Payment> findAll();
    
    public List<Payment> findRepayments();
    
    public List<Payment> findRepayments(int type);
    
    public List<Payment> findRepayments(int type, int st);
    
    public List<Payment> findRepayments(Person p, int type, int st);
    
    public Currency getCurrency();
}
