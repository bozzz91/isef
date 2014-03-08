package ru.desu.home.isef.repo;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;

public interface TaskRepo extends JpaRepository<Task, Long> {

    //public Page<Task> findByOwner(Person p, Pageable pg);
    public List<Task> findByOwner(Person p, Sort sort);
    
    public List<Task> findByStatus(Status p, Sort sort);

    //@Query("from Task t LEFT JOIN t.executors c WHERE t.moderated is true and t.owner <> ?1 and (c.pk.person <> ?1 or c.pk.person is null)")
    //public Page<Task> findTasksForWork(Person p, Pageable pg);
    
    @Query("select t from Task t WHERE t.status.id = 3 and t.owner <> ?1 "
            + "and (t not in (select pt.pk.task from PersonTask pt where pt.pk.person = ?1))")
    public List<Task> findTasksForWork(Person p, Sort sort);

    //@Query("from Task t WHERE t.status = ?2 and t.owner = ?1")
    //public Page<Task> findMyTasksOnExec(Person p, Status st, Pageable pg);
    
    @Query("from Task t WHERE t.status = ?2 and t.owner = ?1")
    public List<Task> findMyTasksByStatus(Person p, Status st, Sort sort);
}
