package ru.desu.home.isef.services.impl;

import java.util.List;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;

@Repository
@Transactional
public class TaskDao extends ParentDao {

    @Transactional(readOnly = true)
    public List<Task> queryAll() {
        Query query = em.createQuery("SELECT l FROM Task l", Task.class);
        List<Task> result = query.getResultList();
        return result;
    }
    
    @Transactional(readOnly = true)
    public List<Task> queryByPerson(Person p) {
        Query query = em.createQuery("SELECT l FROM Task l where l.owner = :p1", Task.class);
        List<Task> result = query.setParameter("p1", p.getId()).getResultList();
        return result;
    }

    @Transactional(readOnly = true)
    public Task get(Long id) {
        return em.find(Task.class, id);
    }

    public Task save(Task t) {
        em.persist(t);
        return t;
    }
    
    public void delete(Task t) {
        Task r = get(t.getTaskId());
        if (r != null) {
            em.remove(r);
        }
    }

    public Task update(Task task) {
        return em.merge(task);
    }
}
