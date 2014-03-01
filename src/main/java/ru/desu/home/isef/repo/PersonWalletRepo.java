package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.PersonWallet;
import ru.desu.home.isef.entity.PersonWalletId;

public interface PersonWalletRepo extends JpaRepository<PersonWallet, PersonWalletId> {
    
}
