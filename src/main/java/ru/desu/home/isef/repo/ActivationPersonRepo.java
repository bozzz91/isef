package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.ActivationPerson;

public interface ActivationPersonRepo extends JpaRepository<ActivationPerson, Long> {

    ActivationPerson findByCode(String code);

    ActivationPerson findByEmail(String code);
}
