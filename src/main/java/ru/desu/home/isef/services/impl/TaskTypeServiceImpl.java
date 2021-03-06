package ru.desu.home.isef.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.TaskType;
import ru.desu.home.isef.repo.TaskTypeRepo;
import ru.desu.home.isef.services.TaskTypeService;

@Service("taskTypeService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TaskTypeServiceImpl implements TaskTypeService {

    @Autowired TaskTypeRepo dao;

    @Override
    public List<TaskType> findAll() {
        return dao.findAll(new Sort(Sort.Direction.ASC, "order"));
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

    @Override
    public List<TaskType> findGroupByType() {
        return dao.findGroupByType();
    }
}
