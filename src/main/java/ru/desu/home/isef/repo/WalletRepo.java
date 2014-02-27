package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.Wallet;

public interface WalletRepo extends JpaRepository<Wallet, Long> {
    
}
