package ru.desu.home.isef.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.TaskType;

public interface TaskTypeRepo extends JpaRepository<TaskType, Long> {

    public List<TaskType> findByCost(Double cost);

    public List<TaskType> findByType(TaskType.Type type);
    
    public List<TaskType> findByTypeAndTaskSize(String type, Integer cost);
    
    @Query("select u from #{#entityName} u group by u.type")
    public List<TaskType> findGroupByType();
    
    @Query("select u from #{#entityName} u where u.type = ?1 group by u.taskSize")
    public List<TaskType> findGroupBySize(String type);
}
