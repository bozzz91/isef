package ru.desu.home.isef.controller;

import java.text.SimpleDateFormat;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.East;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.TaskTypeService;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public abstract class MyTaskListAbstractController extends SelectorComposer<Component> {
    
    //wire components
    @Wire
    Listbox taskList;
    @Wire
    East curTaskEastBlock;
    @Wire
    Textbox curTaskDescription, curTaskConfirm, curTaskLink;
    @Wire
    Label curTaskDate, labelTaskType, curTaskSubject;
    @Wire
    Button closeTask;
    
    //live data model
    ListModelList<Task> taskListModel;
    
    //services
    @WireVariable
    TaskService taskService;
    @WireVariable
    AuthenticationService authService;
    @WireVariable
    PersonService personService;
    @WireVariable
    TaskTypeService taskTypeService;
            
    Task curTask;

    protected void refreshDetailView() {
        //refresh the detail view of selected todo
        if (curTask == null) {
            //clean
            curTaskEastBlock.setOpen(false);
            curTaskEastBlock.setVisible(false);
            curTaskSubject.setValue(null);
            curTaskDate.setValue(null);
            curTaskDescription.setValue(null);
            labelTaskType.setValue(null);
            curTaskLink.setValue(null);
            curTaskConfirm.setValue(null);
        } else {
            curTaskEastBlock.setVisible(true);
            curTaskEastBlock.setOpen(true);
            curTaskSubject.setValue(curTask.getSubject());
            curTaskDate.setValue(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(curTask.getCreationTime()));
            curTaskDescription.setValue(curTask.getDescription());
            labelTaskType.setValue(curTask.getTaskType()
                    +" (Кликов: "+curTask.getCount()
                    +", стоимость: "+curTask.getCost()
                    +", за клик: "+curTask.getTaskType().getGift()+")");
            String link = curTask.getLink();
            int idx_1 = link.indexOf("//")+2;
            int idx_2 = link.indexOf("/", 9) != -1 ? link.indexOf("/", 9) : link.length();
            curTaskLink.setValue(link.substring(idx_1, idx_2));
            curTaskConfirm.setValue(curTask.getConfirmation());
        }
    }
    
    @Listen("onClick = #closeTask")
    public void closeSelectedTaskClick() {
        taskListModel.removeFromSelection(curTask);
        curTask = null;
        refreshDetailView();
    }
    
    @Listen("onSelect = #taskList")
    public void doTaskSelect() {
        if (taskListModel.isSelectionEmpty()) {
            //just in case for the no selection
            curTask = null;
        } else {
            curTask = taskListModel.getSelection().iterator().next();
        }
        refreshDetailView();
    }
    
    protected void doneTask(Task t) {
        t.setStatus(Status._4_DONE);
        taskService.done(t);
        curTask = null;
        refreshDetailView();
        Clients.showNotification("Задание выполнено", "info", null, "middle_center", 2000, true);
    }
}
