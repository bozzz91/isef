package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.*;

import java.util.List;

public interface TaskService {

    Task getTask(Long id);
    
    List<Task> getTasks();

    List<Task> getTasksByOwner(Person p);

    List<Task> getTasksForWork(Person p);
    
    List<Task> getTasksByOwnerAndStatus(Person p, Status st);
    
    List<Task> getTasksByStatus(Status st);

    void delete(Task task);

    Task save(Task task);
    
    Question save(Question question);
    
    Answer save(Answer answer);

    void done(Task selectedTodo);
    
    void donePersonTask(PersonTask pt);
    
    void cancelPersonTask(PersonTask pt);
    
    Task saveTaskAndPerson(Task t, Person p);
    
    List<PersonTask> getExecutorsAll(Task t);
    
    List<PersonTask> getExecutorsForConfirm(Task t);
    
    PersonTask findPersonTask(Task t, Person p);

    List<Object[]> getTaskForWorkRemark(Person p);
    
    int refreshAllTasks();
    
    int refreshMyTasks(Long id);
}
