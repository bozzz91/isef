package ru.desu.home.isef.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.desu.home.isef.entity.TaskType;

import java.util.List;

public interface TaskTypeRepo extends JpaRepository<TaskType, Long> {
    
    @Query("select u from TaskType u group by u.type")
    List<TaskType> findGroupByType();
}
