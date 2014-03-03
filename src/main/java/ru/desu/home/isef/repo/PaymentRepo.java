package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Payment;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

}
