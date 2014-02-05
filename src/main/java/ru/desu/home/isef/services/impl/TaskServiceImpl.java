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

@Service("TaskService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskDao dao;

    @Override
    public Task create(Task task) {
        return dao.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasks() {
        return dao.queryAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByPerson(Person p) {
        return dao.queryByPerson(p);
    }

    @Override
    public void delete(Task task) {
        dao.delete(task);
    }

    @Override
    public Task update(Task task) {
        return dao.update(task);
    }

}
