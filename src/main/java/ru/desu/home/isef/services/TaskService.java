package ru.desu.home.isef.services;

import java.util.List;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;

public interface TaskService {

    Task getTask(Long id);
    
    List<Task> getTasks();

    List<Task> getTasksByOwner(Person p);

    List<Task> getTasksForWork(Person p);
    
    List<Task> getTasksByOwnerAndPublish(Person p, boolean publish);
    
    List<Task> getTasksByOwnerAndDone(Person p, boolean done);

    void delete(Task task);

    Task save(Task task);

    public void done(Task selectedTodo);
    
    public Task saveTaskAndPerson(Task t, Person p);
}
