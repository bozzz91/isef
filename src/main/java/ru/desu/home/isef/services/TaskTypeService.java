package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.TaskType;

import java.util.List;

public interface TaskTypeService {

    List<TaskType> findAll();

    List<TaskType> findGroupByType();

    TaskType find(Long id);

    TaskType save(TaskType p);

    void delete(TaskType p);
}
