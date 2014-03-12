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
import ru.desu.home.isef.entity.TaskType;
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
        return dao.findByOwner(p, new Sort(Sort.Direction.ASC, "remark", "creationTime"));
    }

    @Override
    public void delete(Task task) {
        dao.delete(task);
    }

    @Override
    public List<Task> getTasksForWork(Person p) {
        List<Task> tasksForWork = dao.findTasksForWork(p.getId(), new Sort(Sort.Direction.ASC, "remark", "creationTime"));
        return tasksForWork;
    }

    @Override
    public List<Task> getTasksByOwnerAndStatus(Person p, Status st) {
        List<Task> tasksOnExec = dao.findMyTasksByStatus(p.getId(), st, new Sort(Sort.Direction.ASC, "remark", "creationTime"));
        return tasksOnExec;
    }

    @Override
    public void done(Task task) {
        dao.findOne(task.getTaskId());
        if (task.getCountComplete() >= task.getCount())
            task.setStatus(Status._4_DONE);
        task = dao.saveAndFlush(task);
        
        TaskType tt = task.getTaskType();
        double gift = tt.getGift();
        for (PersonTask pt : task.getExecutors()) {
            if (pt.getStatus() != 1) {
                Person p = pt.getPk().getPerson();
                Person inv = p.getInviter();
                p.addCash(gift);
                pt.setStatus(1);
                personRepo.save(p);
                if (inv != null) {
                    inv.addCash(tt.getGiftReferal());
                    personRepo.save(inv);
                }
                ptRepo.save(pt);
            }
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
        Person inviter = p.getInviter();
        TaskType tt = pt.getTask().getTaskType();
        p.addCash(tt.getGift());
        personRepo.save(p);
        if (inviter != null) {
            inviter.addCash(tt.getGiftReferal());
            personRepo.save(inviter);
        }
    }
    
    @Override
    public void cancelPersonTask(PersonTask pt) {
        ptRepo.save(pt);
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

    @Override
    public List<Object[]> getTaskForWorkRemark(Person p) {
        return dao.findTasksForWorkRemark(p.getId());
    }
}
