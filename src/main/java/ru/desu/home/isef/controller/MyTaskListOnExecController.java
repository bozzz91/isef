package ru.desu.home.isef.controller;

import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListOnExecController extends MyTaskListAbstractController {

    private static final long serialVersionUID = 1L;

    @Wire
    Listbox executorsList;
    @Wire
    Button showExecutors;

    ListModelList<PersonTask> executors;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksByOwnerAndStatus(p, Status._3_PUBLISH);
        taskListModel = new ListModelList<>(todoList);
        taskList.setModel(taskListModel);
    }

    @Override
    protected void refreshDetailView() {
        super.refreshDetailView();
        executorsList.setModel((ListModelList)null);
        showExecutors.setLabel("Показать исполнителей");
        executorsList.setVisible(false);
    }

    @Listen("onClick = #showExecutors")
    public void doShowExecutors() {
        if (executorsList.isVisible()) {
            executors = null;
            executorsList.setVisible(false);
            showExecutors.setLabel("Показать исполнителей");
        } else {
            curTask = taskService.getTask(curTask.getTaskId());
            Set<PersonTask> pts = curTask.getExecutors();

            executors = new ListModelList<>(pts);
            executorsList.setModel(executors);
            executorsList.setVisible(true);
            showExecutors.setLabel("Скрыть исполнителей");
        }
    }
}
