package ru.desu.home.isef.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.East;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.TaskTypeService;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListOnDoneController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    //wire components
    @Wire
    Textbox todoSubject;
    @Wire
    Listbox todoListbox;

    @Wire
    East selectedTodoBlock;
    @Wire
    Label selectedTodoSubject;
    @Wire
    Label selectedTodoDate;
    @Wire
    Label labelTaskType;
    @Wire
    Textbox selectedTodoDescription;

    //services
    @WireVariable
    TaskService taskService;
    @WireVariable
    AuthenticationService authService;
    @WireVariable
    PersonService personService;
    @WireVariable
    TaskTypeService taskTypeService;

    //data for the view
    ListModelList<Task> todoListModel;
    Task selectedTodo;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        //get data from service and wrap it to list-model for the view
        Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksByOwnerAndStatus(p, Status._4_DONE);
        todoListModel = new ListModelList<>(todoList);
        todoListbox.setModel(todoListModel);
    }

    @Listen("onClick = #closeSelectedTodo")
    public void closeSelectedTaskClick() {
        todoListModel.removeFromSelection(selectedTodo);
        selectedTodo = null;
        refreshDetailView();
    }

    @Listen("onClick = #applySelectedTodo")
    public void applyTask() {
        int index = todoListModel.indexOf(selectedTodo);

        selectedTodo.setStatus(Status._4_DONE);
        taskService.done(selectedTodo);

        todoListModel.remove(index);

        selectedTodo = null;
        refreshDetailView();

        //show message for user
        Clients.showNotification("Задание выполнено!");
    }

    //when user checks on the checkbox of each todo on the list
    @Listen("onTodoCheck = #todoListbox")
    public void doTodoCheck(ForwardEvent evt) {
        //get data from event
        Checkbox cbox = (Checkbox) evt.getOrigin().getTarget();
        Listitem litem = (Listitem) cbox.getParent().getParent();

        boolean checked = cbox.isChecked();
        Task todo = litem.getValue();
        todo.setStatus(Status._4_DONE);

        //save data
        todo = taskService.save(todo);
        if (todo.equals(selectedTodo)) {
            selectedTodo = todo;
            //refresh detail view
            refreshDetailView();
        }
        //update listitem style
        ((Listitem) cbox.getParent().getParent()).setSclass(checked ? "complete-todo" : "");
    }

    //when user selects a todo of the listbox
    @Listen("onSelect = #todoListbox")
    public void doTodoSelect() {
        if (todoListModel.isSelectionEmpty()) {
            //just in case for the no selection
            selectedTodo = null;
        } else {
            selectedTodo = todoListModel.getSelection().iterator().next();
        }
        refreshDetailView();
    }

    private void refreshDetailView() {
        //refresh the detail view of selected todo
        if (selectedTodo == null) {
            //clean
            selectedTodoBlock.setOpen(false);
            selectedTodoBlock.setVisible(false);
            selectedTodoSubject.setValue(null);
            selectedTodoDate.setValue(null);
            labelTaskType.setValue(null);
            selectedTodoDescription.setValue(null);
        } else {
            selectedTodoBlock.setVisible(true);
            selectedTodoBlock.setOpen(true);
            selectedTodoSubject.setValue(selectedTodo.getSubject());
            selectedTodoDate.setValue(selectedTodo.getCreationTime().toString());
            labelTaskType.setValue(selectedTodo.getTaskType().toString());
            selectedTodoDescription.setValue(selectedTodo.getDescription());
        }
    }

    //when user clicks the update button
    @Listen("onClick = #reloadSelectedTodo")
    public void doReloadClick() {
        refreshDetailView();
    }
}
