package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.ActivationPerson;

public interface ActivationPersonService {

    public ActivationPerson findByCode(String email);
    
    public ActivationPerson findByEmail(String email);
    
    public ActivationPerson findById(long id);

    public ActivationPerson save(ActivationPerson p);
    
    public void delete(ActivationPerson p);
}
