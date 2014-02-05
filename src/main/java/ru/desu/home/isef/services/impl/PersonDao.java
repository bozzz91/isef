package ru.desu.home.isef.services.impl;

import java.util.List;
import java.util.logging.Level;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import lombok.extern.java.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Person;

@Log
@Repository
@Transactional
public class PersonDao extends ParentDao {

    @Transactional(readOnly = true)
    public List<Person> queryAll() {
        Query query = em.createQuery("SELECT l FROM Person l", Person.class);
        List<Person> result = query.getResultList();
        return result;
    }
    
    @Transactional(readOnly = true)
    public List<Person> queryByEmail(String mail) {
        Query query = em.createQuery("SELECT l FROM Person l where l.email = :p1", Person.class);
        List<Person> result = query.setParameter("p1", mail).getResultList();
        return result;
    }

    @Transactional(readOnly = true)
    public Person get(Long id) {
        return em.find(Person.class, id);
    }
    
    @Transactional(readOnly = true)
    public Person get(String id) {
        TypedQuery<Person> query = em.createQuery("SELECT l FROM Person l where l.email = :p1", Person.class);
        Person result = null;
        try {
            result = query.setParameter("p1", id).getSingleResult();
        } catch (NoResultException ex) {
            log.log(Level.SEVERE, "Empty result, no person fount");
        }
        return result;
    }

    public Person save(Person t) {
        em.persist(t);
        return t;
    }

    public void delete(Person t) {
        Person r = get(t.getId());
        if (r != null) {
            em.remove(r);
        }
    }

    public Person update(Person task) {
        return em.merge(task);
    }
}
