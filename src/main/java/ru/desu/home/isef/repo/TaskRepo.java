package ru.desu.home.isef.repo;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;

public interface TaskRepo extends JpaRepository<Task, Long> {

    public List<Task> findByOwner(Person p);
    
    @Query("select t from Task t where t.owner <> ?1 and t not in ?2")
    public List<Task> findByTaskIdNotInExecuted(Person p, Collection<Task> ids);
}
