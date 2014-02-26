package ru.desu.home.isef.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.TaskType;

public interface TaskTypeRepo extends JpaRepository<TaskType, Long> {
    
    @Query("select u from #{#entityName} u group by u.type")
    public List<TaskType> findGroupByType();
}
