package ru.desu.home.isef.services;

import java.util.List;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;

public interface TaskService {

    List<Task> getTasks();
    
    List<Task> getTasksByOwner(Person p);
    
    List<Task> getTasksForWork(Person p);

    void delete(Task task);
    
    Task save(Task task);
}
