package ru.desu.home.isef.controller;

import java.util.List;
import org.zkoss.lang.Strings;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.East;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListOnModerController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    //wire components
    @Wire
    Textbox curTaskDescription, curTaskLink, curTaskConfirm, curTaskRemark;
    @Wire
    Listbox taskList;
    @Wire
    East curTaskEastBlock;
    @Wire
    Label curTaskSubject, curTaskDate, labelTaskType;

    //services
    @WireVariable
    TaskService taskService;
    @WireVariable
    AuthenticationService authService;

    //data for the view
    ListModelList<Task> todoListModel;
    Task curTask;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        //get data from service and wrap it to list-model for the view
        Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksByStatus(Status._2_MODER);
        todoListModel = new ListModelList<>(todoList);
        taskList.setModel(todoListModel);
    }

    @Listen("onClick = #closeTask")
    public void closeSelectedTaskClick() {
        todoListModel.removeFromSelection(curTask);
        curTask = null;
        refreshDetailView();
    }
    
    //when user selects a todo of the listbox
    @Listen("onSelect = #taskList")
    public void doTodoSelect() {
        if (todoListModel.isSelectionEmpty()) {
            //just in case for the no selection
            curTask = null;
        } else {
            curTask = todoListModel.getSelection().iterator().next();
        }
        refreshDetailView();
    }

    private void refreshDetailView() {
        //refresh the detail view of selected todo
        if (curTask == null) {
            //clean
            curTaskEastBlock.setOpen(false);
            curTaskEastBlock.setVisible(false);
            curTaskSubject.setValue(null);
            curTaskDate.setValue(null);
            labelTaskType.setValue(null);
            curTaskDescription.setValue(null);
            curTaskLink.setValue(null);
            curTaskConfirm.setValue(null);
        } else {
            curTaskEastBlock.setVisible(true);
            curTaskEastBlock.setOpen(true);
            curTaskSubject.setValue(curTask.getSubject());
            curTaskDate.setValue(curTask.getCreationTime().toString());
            labelTaskType.setValue(curTask.getTaskType().toString());
            curTaskDescription.setValue(curTask.getDescription());
            curTaskLink.setValue(curTask.getLink());
            curTaskConfirm.setValue(curTask.getConfirmation());
        }
    }

    @Listen("onClick = #publishTask")
    public void doPublishTask() {
        StringBuilder msg = new StringBuilder("Публикация задания");
        Messagebox.show(msg.toString(),
                "Подтверждение публикации",
                Messagebox.YES | Messagebox.CANCEL,
                Messagebox.QUESTION,
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        if (event.getName().equals(Messagebox.ON_YES)) {
                            int index = todoListModel.indexOf(curTask);
                            curTask.setStatus(Status._3_PUBLISH);
                            taskService.save(curTask);
                            todoListModel.remove(index);
                            curTask = null;
                            refreshDetailView();
                            Clients.showNotification("Задание сохранено и опубликовано", "info", null, "middle_center", 5000);
                        }
                    }
                });
    }
    
    @Listen("onClick = #cancelTask")
    public void doCancelTask() {
        if(Strings.isBlank(curTaskRemark.getValue())) {
            Clients.showNotification("Введите причину отказа", "warning", curTaskRemark, "after_end", 3000);
            return;
        }
        
        StringBuilder msg = new StringBuilder("Отказ задания");
        Messagebox.show(msg.toString(),
                "Отказ публикации",
                Messagebox.YES | Messagebox.CANCEL,
                Messagebox.QUESTION,
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        if (event.getName().equals(Messagebox.ON_YES)) {
                            int index = todoListModel.indexOf(curTask);
                            curTask.setStatus(Status._1_DRAFT);
                            curTask.setRemark(curTaskRemark.getValue());
                            taskService.save(curTask);
                            todoListModel.remove(index);
                            curTask = null;
                            refreshDetailView();
                            Clients.showNotification("Задание отказано и возвращено автору", "info", null, "middle_center", 5000);
                        }
                    }
                });
    }
}
