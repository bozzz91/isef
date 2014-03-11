package ru.desu.home.isef.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
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
        executorsList.setModel((ListModelList) null);
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
            List<PersonTask> pts = taskService.getExecutorsForConfirm(curTask);

            executors = new ListModelList<>(pts);
            executorsList.setModel(executors);
            executorsList.setVisible(true);
            showExecutors.setLabel("Скрыть исполнителей");
        }
    }

    @Listen("onPTDone = #executorsList")
    public void doApplyPT(ForwardEvent evt) {
        Listitem litem = (Listitem) evt.getOrigin().getTarget().getParent().getParent();
        PersonTask t = litem.getValue();
        donePersonTask(t);
        litem.getParent().removeChild(litem);
    }
    
    @Listen("onPTCancel = #executorsList")
    public void doCancelPT(ForwardEvent evt) {
        Listitem litem = (Listitem) evt.getOrigin().getTarget().getParent().getParent();
        PersonTask t = litem.getValue();
        cancelPersonTask(t);
        litem.getParent().removeChild(litem);
    }

    protected void donePersonTask(PersonTask pt) {
        pt.setStatus(1);
        taskService.donePersonTask(pt);
    }
    
    protected void cancelPersonTask(PersonTask pt) {
        pt.setStatus(2);
        taskService.cancelPersonTask(pt);
    }
    
    @Listen("onClick = #applyTask")
    public void applyTask() {
        int index = taskListModel.indexOf(curTask);
        doneTask(curTask, false);
        curTask = taskService.getTask(curTask.getTaskId());
        refreshDetailView();
        taskListModel.set(index, curTask);
    }
}
