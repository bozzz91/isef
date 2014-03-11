package ru.desu.home.isef.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.zkoss.lang.Strings;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;
import org.zkoss.zul.A;
import org.zkoss.zul.Row;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.PersonTaskId;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TodoListController extends MyTaskListAbstractController {

    private static final long serialVersionUID = 1L;

    //wire components
    @Wire
    Textbox taskSubject, curTaskRemark;
    @Wire
    Timer timer;
    @Wire
    Button searchTask, cancelSearch, clBusy;
    @Wire
    Window busyWin;
    @Wire
    Row rowRemark;   

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksForWork(p);
        List<Object[]> ptInfo = taskService.getTaskForWorkRemark(p);
        for (Object[] arr : ptInfo) {
            Long taskId = (Long)arr[0];
            String remr = (String)arr[1];
            for (Task t : todoList) {
                if (t.getTaskId().equals(taskId)) {
                    t.setRemark(remr);
                }
            }
        }
        
        taskListModel = new ListModelList<>(todoList);
        taskList.setModel(taskListModel);
        
        clBusy = (Button) busyWin.getFellow("clBusy");
    }
    
    @Override
    @Listen("onSelect = #taskList")
    public void doTaskSelect() {
        if (taskListModel.isSelectionEmpty()) {
            curTask = null;
        } else {
            curTask = taskListModel.getSelection().iterator().next();
            rowRemark.setVisible(false);
        }
        refreshDetailView();
    }
    
    @Override
    protected void refreshDetailView() {
        super.refreshDetailView();
        if (curTask == null) {
            curTaskRemark.setValue(null);
            rowRemark.setVisible(false);
        } else {
            if (!Strings.isBlank(curTask.getRemark())) {
                curTaskRemark.setValue(curTask.getRemark());
                rowRemark.setVisible(true);
            }
        }
    }

    @Listen("onClick = #searchTask; onOK = #taskSubject")
    public void doSearchTask() {
        if (Strings.isBlank(taskSubject.getValue())) {
            Clients.showNotification("Задайте что ищем", "error", taskSubject, "after_start", 2000);
            return;
        }
        for (Listitem li : taskList.getItems()) {
            Task t = li.getValue();
            if (!t.getSubject().contains(taskSubject.getValue())) {
                li.setVisible(false);
            } else {
                li.setVisible(true);
            }
        }
        cancelSearch.setVisible(true);
        cancelSearch.getParent().invalidate();
    }

    @Listen("onClick = #cancelSearch")
    public void doCancelSearchTask() {
        for (Listitem li : taskList.getItems()) {
            li.setVisible(true);
        }
        cancelSearch.setVisible(false);
        cancelSearch.getParent().invalidate();
    }

    @Listen("onClick = #execTask")
    public void doExecTask() {
        String link = curTask.getLink();
        A a = (A) busyWin.getFellow("link");
        a.setHref(link);
        if (!clBusy.getEventListeners(Events.ON_CLICK).iterator().hasNext()) {
            clBusy.addEventListener(Events.ON_CLICK, new SerializableEventListener<Event>() {

                @Override
                public void onEvent(Event event) throws Exception {
                    doClBusy();
                }
            });
        }
        busyWin.doHighlighted();
        timer.start();
        Clients.showBusy(clBusy, "Выполнение задания");
    }

    public void doClBusy() {
        busyWin.doOverlapped();
        busyWin.setVisible(false);
        try {
            if (!Strings.isBlank(curTask.getConfirmation())) {
                Window doConfirmWin = (Window) Executions.createComponents("/work/todolist/confirmWindow.zul", null, null);
                doConfirmWin.setPosition("center,center");
                doConfirmWin.setDraggable("false");
                doConfirmWin.addEventListener(Events.ON_CLOSE, new SerializableEventListener<Event>() {

                    @Override
                    public void onEvent(Event event) throws Exception {
                        if ((Boolean) event.getData() == true) {
                            String conf = ((Textbox) event.getTarget().getFellow("confirm")).getValue();
                            execTask(conf);
                        }
                    }
                });
                ((Label) doConfirmWin.getFellow("todoLabel")).setValue(curTask.getDescription());
                ((Label) doConfirmWin.getFellow("confirmLabel")).setValue(curTask.getConfirmation());
                ((Label) doConfirmWin.getFellow("ipLabel")).setValue(getIp());
                doConfirmWin.doHighlighted();
            } else {
                execTask("");
                Clients.showNotification("Готово", "info", null, "middle_center", 1000, true);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString());
            Clients.clearBusy(clBusy);
        }
    }

    @Listen("onTimer = #timer")
    public void saveExecResult() {
        timer.stop();
        Clients.clearBusy(busyWin.getFellow("clBusy"));
    }

    private void execTask(String confirm) {
        final int index = taskListModel.indexOf(curTask);
        curTask = taskService.getTask(curTask.getTaskId());
        Person p = authService.getUserCredential().getPerson();
        PersonTask pt = taskService.findPersonTask(curTask, p);
        boolean needInc = true;
        if (pt == null) {
            pt = new PersonTask();
            pt.setPk(new PersonTaskId(p, curTask));
            needInc = false;
        }
        pt.setAdded(new Date());
        pt.setIp(getIp());
        pt.setConfirm(confirm);
        pt.setStatus(0);
        curTask.getExecutors().add(pt);
        if (needInc && curTask.incCountComplete() >= curTask.getCount()) {
            curTask.setStatus(Status._4_DONE);
        }
        curTask = taskService.save(curTask);

        taskListModel.remove(index);
        curTask = null;
        refreshDetailView();
    }

    private String getIp() {
        String ip = Executions.getCurrent().getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = Executions.getCurrent().getRemoteAddr();
        }
        return ip;
    }
}
