package ru.desu.home.isef.services.impl;

import java.util.List;
import javax.persistence.Query;
import lombok.extern.java.Log;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.TaskType;

@Log
@Repository
@Transactional
public class TaskTypeDao extends ParentDao {

    @Transactional(readOnly = true)
    public List<TaskType> findAll() {
        Query query = em.createQuery("SELECT l FROM TaskType l", TaskType.class);
        List<TaskType> result = query.getResultList();
        return result;
    }
    
    @Transactional(readOnly = true)
    public List<TaskType> findByCost(Double cost) {
        Query query = em.createQuery("SELECT l FROM TaskType l where l.cost = :p1", TaskType.class);
        List<TaskType> result = query.setParameter("p1", cost).getResultList();
        return result;
    }
    
    @Transactional(readOnly = true)
    public List<TaskType> findByType(TaskType.Type type) {
        Query query = em.createQuery("SELECT l FROM TaskType l where l.type = :p1", TaskType.class);
        List<TaskType> result = query.setParameter("p1", type).getResultList();
        return result;
    }

    @Transactional(readOnly = true)
    public TaskType get(Long id) {
        return em.find(TaskType.class, id);
    }

    public TaskType save(TaskType t) {
        em.persist(t);
        return t;
    }

    public void delete(TaskType t) {
        TaskType r = get(t.getTaskTypeId());
        if (r != null) {
            em.remove(r);
        }
    }

    public TaskType update(TaskType task) {
        return em.merge(task);
    }
}
