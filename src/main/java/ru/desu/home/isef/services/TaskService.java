package ru.desu.home.isef.services;

import java.util.List;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;

public interface TaskService {

    Task getTask(Long id);
    
    List<Task> getTasks();

    List<Task> getTasksByOwner(Person p);

    List<Task> getTasksForWork(Person p);
    
    List<Task> getTasksByOwnerAndStatus(Person p, Status st);
    
    List<Task> getTasksByStatus(Status st);

    void delete(Task task);

    Task save(Task task);

    public void done(Task selectedTodo);
    
    public void donePersonTask(PersonTask pt);
    
    public Task saveTaskAndPerson(Task t, Person p);
    
    public List<PersonTask> getExecutorsAll(Task t);
    
    public List<PersonTask> getExecutorsForConfirm(Task t);
    
    public PersonTask findPersonTask(Task t, Person p);
}
