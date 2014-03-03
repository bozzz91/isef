package ru.desu.home.isef.services;

import java.util.List;
import ru.desu.home.isef.entity.Currency;
import ru.desu.home.isef.entity.Payment;

public interface PaymentService {

    public Payment findOne(Long id);
    
    public Payment save(Payment p);
    
    public void delete(Payment p);
    
    public List<Payment> findAll();
    
    public Currency getCurrency();
}
