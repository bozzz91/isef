package ru.desu.home.isef.services.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.*;
import ru.desu.home.isef.repo.AnswerRepo;
import ru.desu.home.isef.repo.PersonRepo;
import ru.desu.home.isef.repo.PersonTaskRepo;
import ru.desu.home.isef.repo.QuestionRepo;
import ru.desu.home.isef.repo.TaskRepo;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.TaskService;

@Service("taskService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TaskServiceImpl implements TaskService {

    @Autowired TaskRepo dao;
    @Autowired PersonRepo personRepo;
    @Autowired PersonTaskRepo ptRepo;
    @Autowired QuestionRepo questionRepo;
    @Autowired AnswerRepo answerRepo;
    @Autowired PersonService personService;

    @Override
    public Task save(Task task) {
        Task saved = dao.saveAndFlush(task);
        return saved;
    }
    
    @Override
    public Question save(Question question) {
        return questionRepo.saveAndFlush(question);
    }
    
    @Override
    public Answer save(Answer answer) {
        return answerRepo.saveAndFlush(answer);
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
        
        for (PersonTask pt : task.getExecutors()) {
            if (pt.getStatus() != 1) {
                donePersonTask(pt);
            }
        }
    }
    
    @Override
    public Task saveTaskAndPerson(Task task, Person p) {
        Task saved = save(task);
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
        pt.setStatus(1);
        pt.setExecuted(new Date());
        Person p = pt.getPerson();
        TaskType tt = pt.getTask().getTaskType();
        p.addCash(tt.getGift(), true);
        
        Person inviter = p.getInviter();
        if (inviter != null) {
            inviter.addCash(tt.getGiftReferal());
            personRepo.save(inviter);
            Rating rate = personService.getRating(inviter);
            double reverseCoefficient = rate.getReverse();
            p.addCash(tt.getGift() * reverseCoefficient);
        }
        ptRepo.save(pt);
        personRepo.save(p);
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

    @Override
    public int refreshAllTasks() {
        return dao.refreshTasks();
    }
    
    @Override
    public int refreshMyTasks(Long id) {
        return dao.refreshTasks(id);
    }
}
