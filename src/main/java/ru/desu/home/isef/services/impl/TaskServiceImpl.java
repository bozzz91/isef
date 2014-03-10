package ru.desu.home.isef.services.impl;

import ru.desu.home.isef.services.TaskService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.PersonTaskId;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.repo.PersonRepo;
import ru.desu.home.isef.repo.PersonTaskRepo;
import ru.desu.home.isef.repo.TaskRepo;

@Service("taskService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepo dao;
    @Autowired
    PersonRepo personRepo;
    @Autowired
    PersonTaskRepo ptRepo;

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
        return dao.findMyTasksByStatus(p, Status._1_DRAFT, new Sort(Sort.Direction.ASC, "remark", "creationTime"));
    }

    @Override
    public void delete(Task task) {
        dao.delete(task);
    }

    @Override
    public List<Task> getTasksForWork(Person p) {
        List<Task> tasksForWork = dao.findTasksForWork(p, new Sort(Sort.Direction.ASC, "remark", "creationTime"));
        return tasksForWork;
    }

    @Override
    public List<Task> getTasksByOwnerAndStatus(Person p, Status st) {
        List<Task> tasksOnExec = dao.findMyTasksByStatus(p, st, new Sort(Sort.Direction.ASC, "remark", "creationTime"));
        return tasksOnExec;
    }

    @Override
    public void done(Task task) {
        dao.findOne(task.getTaskId());
        task = dao.saveAndFlush(task);
        
        double gift = task.getTaskType().getGift();
        for (PersonTask pt : task.getExecutors()) {
            Person p = pt.getPk().getPerson();
            p.addCash(gift);
            personRepo.save(p);
        }
    }
    
    @Override
    public Task saveTaskAndPerson(Task t, Person p) {
        Task saved = dao.saveAndFlush(t);
        personRepo.saveAndFlush(p);
        return saved;
    }

    @Override
    public Task getTask(Long id) {
        return dao.findOne(id);
    }

    @Override
    public List<Task> getTasksByStatus(Status st) {
        List<Task> res = dao.findByStatus(st, new Sort(Sort.Direction.ASC, "modificationTime"));
        return res;
    }

    @Override
    public void donePersonTask(PersonTask pt) {
        ptRepo.save(pt);
        Person p = pt.getPerson();
        p.addCash(pt.getTask().getTaskType().getGift());
        personRepo.save(p);
        Task t = dao.findOne(pt.getTask().getTaskId());
        if (t.getCountComplete() >= t.getCount()) {
            t.setStatus(Status._4_DONE);
        }
        dao.save(t);
    }

    @Override
    public List<PersonTask> getExecutorsAll(Task t) {
        return dao.findExecutorsAllForTask(t.getTaskId());
    }

    @Override
    public List<PersonTask> getExecutorsForConfirm(Task t) {
        return dao.findExecutorsForConfirm(t.getTaskId());
    }

    @Override
    public PersonTask findPersonTask(Task t, Person p) {
        PersonTaskId id = new PersonTaskId(p, t);
        return ptRepo.findOne(id);
    }
}
