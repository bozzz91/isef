package ru.desu.home.isef.services.impl;

import ru.desu.home.isef.services.TaskService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.repo.TaskRepo;

@Service("TaskService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepo dao;

    @Override
    public Task save(Task task) {
        return dao.saveAndFlush(task);
    }

    @Override
    public List<Task> getTasks() {
        return dao.findAll();
    }

    @Override
    public List<Task> getTasksByOwner(Person p) {
        return dao.findByOwner(p);
    }

    @Override
    public void delete(Task task) {
        dao.delete(task);
    }

    @Override
    public List<Task> getTasksForWork(Person p) {
        /*val executedTasks = new HashSet<>(p.getExecutedTasks());
        val tasks = new ArrayList<>(p.getTasks());
        executedTasks.addAll(tasks);
        List<Task> tasksForWork = dao.findByTaskIdNotInExecuted(p, executedTasks);*/
        List<Task> tasksForWork = dao.findByTaskIdNotInExecuted(p);
        dao.findAll(new PageRequest(0, 10));
        return tasksForWork;
    }

}
