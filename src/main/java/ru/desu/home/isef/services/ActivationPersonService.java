package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.ActivationPerson;

public interface ActivationPersonService {

    ActivationPerson findByCode(String email);

    ActivationPerson findByEmail(String email);

    ActivationPerson findById(long id);

    ActivationPerson save(ActivationPerson p);

    void delete(ActivationPerson p);
}
