package ru.desu.home.isef.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zul.ListModelList;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;

import java.util.List;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListOnModerController extends MyTaskListAbstractController {
    private static final long serialVersionUID = 1L;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

		Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksByOwnerAndStatus(p, Status._2_MODER);
        taskListModel = new ListModelList<>(todoList);
        taskList.setModel(taskListModel);
    }
}
