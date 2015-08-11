package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.repo.PaymentRepo;
import ru.desu.home.isef.services.PaymentService;
import ru.desu.home.isef.utils.ConfigUtil;

import java.util.List;

@Service("paymentService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PaymentServiceImpl implements PaymentService {

    @Autowired PaymentRepo dao;
    @Autowired ConfigUtil config;
    
    @Override
    public Payment findOne(Long id) {
        return dao.findOne(id);
    }

    @Override
    public Payment save(Payment p) {
        return dao.save(p);
    }

    @Override
    public void delete(Payment p) {
        dao.delete(p);
    }

    @Override
    public List<Payment> findAll() {
        return dao.findAll();
    }

    @Override
    public Double getCurrency() {
        return config.getCurrency();
    }
    
    @Override
    public List<Payment> findRepayments() {
        return dao.findAll(new Sort(Sort.Direction.ASC, "orderDate"));
    }
    
    @Override
    public List<Payment> findRepayments(int type) {
        return dao.findByType(type, new Sort(Sort.Direction.ASC, "orderDate"));
    }

    @Override
    public List<Payment> findRepayments(int type, int st) {
        return dao.findByTypeAndStatus(type, st, new Sort(Sort.Direction.ASC, "orderDate"));
    }

    @Override
    public List<Payment> findRepayments(Person p, int type, int st) {
        return dao.findByPayOwnerAndTypeAndStatus(p, type, st, new Sort(Sort.Direction.ASC, "orderDate"));
    }
}
