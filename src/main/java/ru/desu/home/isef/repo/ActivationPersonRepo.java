package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.ActivationPerson;

public interface ActivationPersonRepo extends JpaRepository<ActivationPerson, Long> {
    public ActivationPerson findByCode(String code);
    
    public ActivationPerson findByEmail(String code);
}