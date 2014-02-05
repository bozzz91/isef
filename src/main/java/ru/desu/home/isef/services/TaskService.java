package ru.desu.home.isef.services;

import java.util.List;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;

public interface TaskService {

    Task create(Task task);

    List<Task> getTasks();
    
    List<Task> getTasksByPerson(Person p);

    void delete(Task task);
    
    Task update(Task task);
}
