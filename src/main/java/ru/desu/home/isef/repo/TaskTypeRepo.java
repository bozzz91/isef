package ru.desu.home.isef.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.desu.home.isef.entity.TaskType;

public interface TaskTypeRepo extends JpaRepository<TaskType, Long> {

    public List<TaskType> findByCost(Double cost);

    public List<TaskType> findByType(String type);
}
