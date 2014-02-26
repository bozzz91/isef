package ru.desu.home.isef.services;

import java.util.List;
import ru.desu.home.isef.entity.TaskType;

public interface TaskTypeService {

    public List<TaskType> findAll();

    public List<TaskType> findGroupByType();

    public TaskType find(Long id);

    public TaskType save(TaskType p);

    public void delete(TaskType p);
}
