package ru.desu.home.isef.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.TaskType;
import ru.desu.home.isef.repo.TaskTypeRepo;
import ru.desu.home.isef.services.TaskTypeService;

@Service("TaskTypeService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TaskTypeServiceImpl implements TaskTypeService {

    @Autowired
    TaskTypeRepo dao;
    //TaskTypeDao dao;

    /*@Override
    @Transactional(readOnly = true)
    public List<TaskType> findAll() {
        return dao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskType find(Long id) {
        return dao.get(id);
    }

    @Override
    public TaskType update(TaskType p) {
        return dao.update(p);
    }

    @Override
    public TaskType add(TaskType p) {
        return dao.save(p);
    }

    @Override
    public void delete(TaskType p) {
        dao.delete(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskType> findByCost(Double cost) {
        return dao.findByCost(cost);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskType> findByType(TaskType.Type type) {
        return dao.findByType(type);
    }*/

    @Override
    public List<TaskType> findAll() {
        return dao.findAll();
    }

    @Override
    public List<TaskType> findByCost(Double cost) {
        return dao.findByCost(cost);
    }

    @Override
    public List<TaskType> findByType(TaskType.Type type) {
        return dao.findByType(type);
    }

    @Override
    public TaskType find(Long id) {
        return dao.findOne(id);
    }

    @Override
    public TaskType save(TaskType p) {
        return dao.saveAndFlush(p);
    }

    @Override
    public void delete(TaskType p) {
        dao.delete(p);
    }
}
