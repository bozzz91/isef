package ru.desu.home.isef.controller;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zul.ListModelList;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.utils.SessionUtil;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListOnDoneController extends MyTaskListOnExecController {

    private static final long serialVersionUID = 1L;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksByOwnerAndStatus(p, Status._4_DONE);
        taskListModel = new ListModelList<>(todoList);
        taskList.setModel(taskListModel);
    }

    @Override
    @Listen("onClick = #applyTask")
    public void applyTask() {
        Task curTask = SessionUtil.getCurTask();
        int index = taskListModel.indexOf(curTask);
        doneTask(curTask);
        SessionUtil.removeCurTask();
        refreshDetailView();
        taskListModel.remove(index);
    }
}
