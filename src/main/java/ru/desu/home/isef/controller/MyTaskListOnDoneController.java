package ru.desu.home.isef.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listitem;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListOnDoneController extends MyTaskListAbstractController {

    private static final long serialVersionUID = 1L;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        //get data from service and wrap it to list-model for the view
        Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksByOwnerAndStatus(p, Status._4_DONE);
        taskListModel = new ListModelList<>(todoList);
        taskList.setModel(taskListModel);
    }
    
    @Listen("onClick = #applyTask")
    public void applyTask() {
        int index = taskListModel.indexOf(curTask);

        curTask.setStatus(Status._4_DONE);
        taskService.done(curTask);

        taskListModel.remove(index);

        curTask = null;
        refreshDetailView();

        //show message for user
        Clients.showNotification("Задание выполнено!");
    }

    //when user checks on the checkbox of each todo on the list
    @Listen("onTaskCheck = #taskList")
    public void doTaskCheck(ForwardEvent evt) {
        //get data from event
        Checkbox cbox = (Checkbox) evt.getOrigin().getTarget();
        Listitem litem = (Listitem) cbox.getParent().getParent();

        boolean checked = cbox.isChecked();
        Task todo = litem.getValue();
        todo.setStatus(Status._4_DONE);

        //save data
        todo = taskService.save(todo);
        if (todo.equals(curTask)) {
            curTask = todo;
            //refresh detail view
            refreshDetailView();
        }
        //update listitem style
        ((Listitem) cbox.getParent().getParent()).setSclass(checked ? "complete-todo" : "");
    }
}
