package ru.desu.home.isef.repo;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;

public interface TaskRepo extends JpaRepository<Task, Long> {

    public Page<Task> findByOwner(Person p, Pageable pg);
    public List<Task> findByOwner(Person p);

    @Query("select t from Task t LEFT JOIN t.executors c WHERE t.publish is true and t.owner <> ?1 and (c.pk.person <> ?1 or c.pk.person is null)")
    public Page<Task> findTasksForWork(Person p, Pageable pg);
    @Query("select t from Task t LEFT JOIN t.executors c WHERE t.publish is true and t.owner <> ?1 and (c.pk.person <> ?1 or c.pk.person is null)")
    public List<Task> findTasksForWork(Person p);

    @Query("select t from Task t WHERE t.publish is ?2 and t.done is false and t.owner = ?1")
    public Page<Task> findMyTasksOnExec(Person p, boolean b, Pageable pg);
    @Query("select t from Task t WHERE t.publish is ?2 and t.done is false and t.owner = ?1")
    public List<Task> findMyTasksOnExec(Person p, boolean b);

    @Query("select t from Task t WHERE t.publish is ?2 and t.done is false and t.owner = ?1")
    public Page<Task> findMyTasksOnDrafts(Person p, boolean b, Pageable pg);
    @Query("select t from Task t WHERE t.publish is ?2 and t.done is false and t.owner = ?1")
    public List<Task> findMyTasksOnDrafts(Person p, boolean b);
    
    @Query("select t from Task t WHERE t.publish is true and t.done is ?2 and t.owner = ?1")
    public Page<Task> findMyTasksOnDone(Person p, boolean b, Pageable pg);
    @Query("select t from Task t WHERE t.publish is true and t.done is ?2 and t.owner = ?1")
    public List<Task> findMyTasksOnDone(Person p, boolean b);
}
