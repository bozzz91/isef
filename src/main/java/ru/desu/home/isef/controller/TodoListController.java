package ru.desu.home.isef.controller;

import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonTask;
import ru.desu.home.isef.entity.PersonTaskId;
import ru.desu.home.isef.entity.Task;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TodoListController extends MyTaskListAbstractController {

    private static final long serialVersionUID = 1L;

    //wire components
    @Wire
    Textbox taskSubject;
    @Wire
    Timer timer;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        //get data from service and wrap it to list-model for the view
        Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksForWork(p);
        taskListModel = new ListModelList<>(todoList);
        taskList.setModel(taskListModel);
    }

    //when user clicks on the button or enters on the textbox
    @Listen("onClick = #searchTask; onOK = #taskSubject")
    public void doSearchTask() {
        Clients.showNotification("Поиск пока отключен (а нужен он тут?)");
    }
    
    @Listen("onClick = #execTask")
    public void doExecTask() {
        String link = curTaskLink.getValue();
        if (!link.startsWith("http://")) {
            link = "http://"+link;
        }
        Clients.evalJavaScript("window.open('" + Executions.encodeURL(link) + "')");
        Events.echoEvent("onOpenLink", timer, null);
        Clients.showBusy("Выполнение задания\n"+curTaskDescription.getValue());
    }
 
    @Listen("onOpenLink = #timer")
    public void processingFiles() {
        timer.start();
    }
 
    @Listen("onTimer = #timer")
    public void fetchingSimulatorTimer() {
        timer.stop();
        try {
            curTask = taskService.getTask(curTask.getTaskId());
            PersonTask pt = new PersonTask();
            pt.setPk(new PersonTaskId(authService.getUserCredential().getPerson(), curTask));
            pt.setAdded(new Date());
            pt.setIp(Executions.getCurrent().getRemoteAddr());
            curTask.getExecutors().add(pt);
            curTask = taskService.save(curTask);
        } finally {
            Clients.clearBusy();
        }
    }
}
