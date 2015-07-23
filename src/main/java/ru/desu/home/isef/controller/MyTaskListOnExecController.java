package ru.desu.home.isef.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;

import java.util.List;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListOnExecController extends MyTaskListAbstractController {

    private static final long serialVersionUID = 1L;

	protected static final String SHOW_EXECUTORS_BTN = "Показать новые выполнения";
	protected static final String HIDE_EXECUTORS_BTN = "Скрыть исполнителей";

    @Wire Listbox executorsList;
    @Wire Button showExecutors;

    protected ListModelList<PersonTask> executors;

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
        showExecutors.setLabel(SHOW_EXECUTORS_BTN);
        executorsList.setVisible(false);

		if (curTask.getTaskType().isSurfing() || curTask.getTaskType().isQuestion()) {
			showExecutors.setVisible(false);
		} else {
			showExecutors.setVisible(true);
		}
    }

    @Listen("onClick = #showExecutors")
    public void doShowExecutors() {
        if (executorsList.isVisible()) {
            executors = null;
            executorsList.setVisible(false);
            showExecutors.setLabel(SHOW_EXECUTORS_BTN);
        } else {
            List<PersonTask> pts = taskService.getExecutorsForConfirm(curTask);

            executors = new ListModelList<>(pts);
            executorsList.setModel(executors);
            executorsList.setVisible(true);
			executorsList.invalidate();
            showExecutors.setLabel(HIDE_EXECUTORS_BTN);
        }
    }

    @Listen("onPTDone = #executorsList")
    public void doApplyPT(ForwardEvent evt) {
        Listitem litem = (Listitem) evt.getOrigin().getTarget().getParent().getParent();
		PersonTask t = litem.getValue();
        donePersonTask(t);
        executors.remove(t);
    }
    
    @Listen("onPTCancel = #executorsList")
    public void doCancelPT(final ForwardEvent evt) {
        Window doConfirmWin = (Window) Executions.createComponents("/work/mytasks/remarkWindow.zul", null, null);
        doConfirmWin.setPosition("center,center");
        doConfirmWin.setDraggable("false");
        doConfirmWin.addEventListener(Events.ON_CLOSE, new SerializableEventListener<Event>() {

            @Override
            public void onEvent(Event event) throws Exception {
                if ((Boolean) event.getData()) {
                    String conf = ((Textbox) event.getTarget().getFellow("remark")).getValue();
                    Listitem litem = (Listitem) evt.getOrigin().getTarget().getParent().getParent();
                    PersonTask pt = litem.getValue();
                    pt.setRemark(conf);
                    cancelPersonTask(pt);
                    executors.remove(pt);
                }
            }
        });
        doConfirmWin.doHighlighted();
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
