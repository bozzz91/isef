package ru.desu.home.isef.services.impl;

import ru.desu.home.isef.services.TaskService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.repo.PersonRepo;
import ru.desu.home.isef.repo.TaskRepo;

@Service("taskService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepo dao;
    @Autowired
    PersonRepo personRepo;

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
        return dao.findMyTasksOnDrafts(p, false);
    }

    @Override
    public void delete(Task task) {
        dao.delete(task);
    }

    @Override
    public List<Task> getTasksForWork(Person p) {
        List<Task> tasksForWork = dao.findTasksForWork(p);
        return tasksForWork;
    }

    @Override
    public List<Task> getTasksByOwnerAndPublish(Person p, boolean publish) {
        List<Task> tasksOnExec = dao.findMyTasksOnExec(p, publish);
        return tasksOnExec;
    }

    @Override
    public List<Task> getTasksByOwnerAndDone(Person p, boolean done) {
        List<Task> tasksOnDone = dao.findMyTasksOnDone(p, done);
        return tasksOnDone;
    }

    @Override
    public void done(Task task) {
        dao.save(task);
        
        double gift = task.getTaskType().getGift();
        for (Person p : task.getExecutors()) {
            p.addCash(gift);
            personRepo.save(p);
        }
    }
}
