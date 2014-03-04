package ru.desu.home.isef.controller;

import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListOnDoneController extends MyTaskListAbstractController {

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
        List<Task> todoList = taskService.getTasksByOwnerAndStatus(p, Status._4_DONE);
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
    
    @Listen("onClick = #applyTask")
    public void applyTask() {
        int index = taskListModel.indexOf(curTask);
        doneTask(curTask);
        taskListModel.remove(index);
    }
    
    @Listen("onClick = #cancelTask")
    public void cancelTask() {
        Clients.showNotification("Отключено пока, уточнить как надо сделать", "warning", null, "middle_center", 10000, true);
    }

    @Listen("onTaskDone = #taskList")
    public void doApplyCheck(ForwardEvent evt) {
        Listitem litem = (Listitem) evt.getOrigin().getTarget().getParent().getParent();
        Task t = litem.getValue();
        doneTask(t);
        litem.getParent().removeChild(litem);
    }
    
    private void doneTask(Task t) {
        t.setStatus(Status._5_TRASH);
        taskService.done(t);
        curTask = null;
        refreshDetailView();
        Clients.showNotification("Задание выполнено", "info", null, "middle_center", 2000, true);
    }
}
