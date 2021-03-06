package ru.desu.home.isef.repo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;

import java.util.List;

public interface TaskRepo extends JpaRepository<Task, Long> {

    //public Page<Task> findByOwner(Person p, Pageable pg);
    List<Task> findByOwner(Person p, Sort sort);
    
    List<Task> findByStatus(Status p, Sort sort);

    //@Query("from Task t LEFT JOIN t.executors c WHERE t.moderated is true and t.owner.id <> ?1 and (c.pk.person.id <> ?1 or c.pk.person.id is null)")
    //public Page<Task> findTasksForWork(Long p, Pageable pg);
    
    @Query("select t from Task t WHERE t.owner.id <> ?1 and "
            + "("
            + "  (  t.status.id = 3 and (t.taskId not in (select pt.pk.task.taskId from PersonTask pt where pt.pk.person.id = ?1 and pt.status <> 2 )) )"
            + "   or "
            + "  (  t.status.id = 4 and (t.taskId     in (select pt.pk.task.taskId from PersonTask pt where pt.pk.person.id = ?1 and pt.status =  2 )) )"
            + ")")
    List<Task> findTasksForWork(Long p, Sort sort);

    @Query("select pt.pk.task.taskId, pt.remark "
           + "from PersonTask pt "
           + "where pt.remark <> '' and pt.remark is not null and pt.pk.person.id = ?1")
    List<Object[]> findTasksForWorkRemark(Long p);

    @Query(value = "select refreshtasks()", nativeQuery = true)
    int refreshTasks();

    @Query(value = "select refreshmytasks(?1)", nativeQuery = true)
    int refreshTasks(Long id);
    
    //@Query("from Task t WHERE t.status = ?2 and t.owner.id = ?1")
    //public Page<Task> findMyTasksOnExec(Long p, Status st, Pageable pg);
    
    @Query("from Task t WHERE t.status = ?2 and t.owner.id = ?1")
    List<Task> findMyTasksByStatus(Long p, Status st, Sort sort);
    
    @Query("select pt from PersonTask pt WHERE pt.pk.task.taskId = ?1")
    List<PersonTask> findExecutorsAllForTask(Long t);
    
    @Query("select pt from PersonTask pt WHERE pt.pk.task.taskId = ?1 and pt.status = 0")
    List<PersonTask> findExecutorsForConfirm(Long t);
}
