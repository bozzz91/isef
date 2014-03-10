package ru.desu.home.isef.controller;

import java.util.Date;
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
    Textbox taskSubject;
    @Wire
    Timer timer;
    @Wire
    Button searchTask, cancelSearch;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        //get data from service and wrap it to list-model for the view
        Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksForWork(p);
        taskListModel = new ListModelList<>(todoList);
        taskList.setModel(taskListModel);
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

        Clients.evalJavaScript("window.open('" + Executions.encodeURL(link) + "')");
        Events.echoEvent("onOpenLink", timer, null);
        Clients.showBusy("Выполнение задания");
    }

    @Listen("onOpenLink = #timer")
    public void processingFiles() {
        timer.start();
    }

    @Listen("onTimer = #timer")
    public void saveExecResult() {
        timer.stop();
        try {
            if (!Strings.isBlank(curTask.getConfirmation())) {
                Window doConfirmWin = (Window) Executions.createComponents("/work/todolist/confirmWindow.zul", null, null);
                ((Label) doConfirmWin.getFellow("confirmLabel")).setValue(curTask.getConfirmation());
                ((Label) doConfirmWin.getFellow("ipLabel")).setValue(getIp());
                doConfirmWin.addEventListener(Events.ON_CLOSE, new SerializableEventListener<Event>() {

                    @Override
                    public void onEvent(Event event) throws Exception {
                        if ((Boolean)event.getData() == true) {
                            String conf = ((Textbox) event.getTarget().getFellow("confirm")).getValue();
                            execTask(conf);
                        }
                    }
                });
                Clients.clearBusy();
                doConfirmWin.doHighlighted();
            } else {
                Clients.clearBusy();
                execTask("");
                Clients.showNotification("Готово", "info", null, "middle_center", 1000, true);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString());
            Clients.clearBusy();
        }
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
