package ru.desu.home.isef.controller;

import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.East;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.entity.TaskType;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.TaskTypeService;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    //wire components
    @Wire
    Textbox todoSubject;
    @Wire
    Button addTodo;
    @Wire
    Listbox todoListbox;
    @Wire
    //Selectbox taskTypeList;
    Combobox taskTypeList;

    @Wire
    East selectedTodoBlock;
    @Wire
    Textbox selectedTodoSubject;
    @Wire
    Label selectedTodoDate;
    @Wire
    Label labelTaskType;
    @Wire
    Textbox selectedTodoDescription;
    @Wire
    Button updateSelectedTodo;
    @Wire
    Label personCashLabel;

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
    ListModelList<TaskType> taskTypesModel;
    ListModelList<Task> todoListModel;
    Task selectedTodo;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        //get data from service and wrap it to list-model for the view
        Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksByOwner(p);
        todoListModel = new ListModelList<>(todoList);
        todoListbox.setModel(todoListModel);

        List<TaskType> types = taskTypeService.findAll();
        taskTypesModel = new ListModelList<>(types);
        taskTypesModel.addToSelection(types.get(0));
        taskTypeList.setModel(taskTypesModel);

        personCashLabel.setValue("Ваш баланс: " + p.getCash());
    }

    @Listen("onClick = #closeSelectedTodo")
    public void closeSelectedTaskClick() {
        todoListModel.removeFromSelection(selectedTodo);
        selectedTodo = null;
        refreshDetailView();
    }

    @Listen("onClick = #publishSelectedTodo")
    public void publishTask() {
        if (Strings.isBlank(selectedTodoSubject.getValue())) {
            Clients.showNotification("Введите название задания", "warning", selectedTodoSubject, "after_end", 3000);
            return;
        }
        if (Strings.isBlank(selectedTodoDescription.getValue())) {
            Clients.showNotification("Напишите что необходимо сделать в вашем задании", "warning", selectedTodoDescription, "before_start", 5000);
            return;
        }
        int index = todoListModel.indexOf(selectedTodo);

        selectedTodo.setPublish(true);
        selectedTodo.setSubject(selectedTodoSubject.getValue());
        selectedTodo.setDescription(selectedTodoDescription.getValue());
		//selectedTodo.setPriority(priorityListModel.getSelection().iterator().next());

        //save data and get updated Todo object
        selectedTodo = taskService.save(selectedTodo);

        //replace original Todo object in listmodel with updated one
        todoListModel.remove(index);

        selectedTodo = null;
        refreshDetailView();

        //show message for user
        Clients.showNotification("Задание сохранено и опубликовано");
    }

    //when user clicks on the button or enters on the textbox
    @Listen("onClick = #addTodo; onOK = #todoSubject")
    public void doTodoAdd() {
        if (taskTypeList.getSelectedIndex() == -1) {
            Clients.showNotification("Выберите тип задания", "warning", taskTypeList, "after_end", 3000);
            return;
        }

        int index = taskTypeList.getSelectedIndex();
        TaskType selectedType = taskTypeList.<TaskType>getModel().getElementAt(index);
        Person p = authService.getUserCredential().getPerson();
        if (p.getCash() < selectedType.getCost()) {
            Clients.showNotification("Недостаточно средств на вашем балансе чтобы создать задачу выбранного типа", "warning", taskTypeList, "after_end", 3000);
            return;
        }

        //get user input from view
        String subject = todoSubject.getValue();
        if (Strings.isBlank(subject)) {
            Clients.showNotification("Придумайте название", "warning", todoSubject, "after_pointer", 3000);
        } else {
            Task t = new Task();
            t.setSubject(subject);
            t.setTaskType(selectedType);
            t.setDescription("");
            t.setOwner(authService.getUserCredential().getPerson());

            p.setCash(p.getCash() - t.getTaskType().getCost());
            
            selectedTodo = taskService.saveTaskAndPerson(t, p);
            authService.getUserCredential().setPerson(p);
            personCashLabel.setValue("Ваш баланс: " + p.getCash());

            //update the model of listbox
            todoListModel.add(selectedTodo);
            //set the new selection
            todoListModel.addToSelection(selectedTodo);

            //refresh detail view
            refreshDetailView();

            //reset value for fast typing.
            todoSubject.setValue("");
        }
    }

    //when user checks on the checkbox of each todo on the list
    @Listen("onTodoCheck = #todoListbox")
    public void doTodoCheck(ForwardEvent evt) {
        //get data from event
        Checkbox cbox = (Checkbox) evt.getOrigin().getTarget();
        Listitem litem = (Listitem) cbox.getParent().getParent();

        boolean checked = cbox.isChecked();
        Task todo = litem.getValue();
        todo.setDone(checked);

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

    //when user clicks the delete button of each todo on the list
    @Listen("onTodoDelete = #todoListbox")
    public void doTodoDelete(ForwardEvent evt) {
        Button btn = (Button) evt.getOrigin().getTarget();
        Listitem litem = (Listitem) btn.getParent().getParent();

        final Task todo = (Task) litem.getValue();

        Messagebox.show("Уверенны что хотите удалить задание?\nЕго стоимость будет возвращена на Ваш счёт.\n\"" + todo.getSubject() + "\"",
                "Подтверждение удаления",
                Messagebox.YES | Messagebox.CANCEL,
                Messagebox.QUESTION,
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        if (event.getName().equals(Messagebox.ON_YES)) {
                            double cost = todo.getTaskType().getCost();

                            //delete data
                            taskService.delete(todo);

                            Person p = authService.getUserCredential().getPerson();
                            p = personService.findById(p.getId());
                            p.setCash(p.getCash() + cost);
                            personService.save(p);
                            authService.getUserCredential().setPerson(p);

                            personCashLabel.setValue("Ваш баланс: " + p.getCash());
                            //update the model of listbox
                            todoListModel.remove(todo);

                            if (todo.equals(selectedTodo)) {
                                //refresh selected todo view
                                selectedTodo = null;
                                refreshDetailView();
                            }
                        }
                    }
                });
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
            updateSelectedTodo.setDisabled(true);
        } else {
            selectedTodoBlock.setVisible(true);
            selectedTodoBlock.setOpen(true);
            selectedTodoSubject.setValue(selectedTodo.getSubject());
            selectedTodoDate.setValue(selectedTodo.getCreationTime().toString());
            labelTaskType.setValue(selectedTodo.getTaskType().toString());
            selectedTodoDescription.setValue(selectedTodo.getDescription());
            updateSelectedTodo.setDisabled(false);
        }
    }

    //when user clicks the update button
    @Listen("onClick = #updateSelectedTodo")
    public void doUpdateClick() {
        if (Strings.isBlank(selectedTodoSubject.getValue())) {
            Clients.showNotification("Введите название задания", "warning", selectedTodoSubject, "after_end", 3000);
            return;
        }
        int index = todoListModel.indexOf(selectedTodo);

        selectedTodo.setSubject(selectedTodoSubject.getValue());
        selectedTodo.setDescription(selectedTodoDescription.getValue());
		//selectedTodo.setPriority(priorityListModel.getSelection().iterator().next());

        //save data and get updated Todo object
        selectedTodo = taskService.save(selectedTodo);

        //replace original Todo object in listmodel with updated one
        todoListModel.set(index, selectedTodo);

        //show message for user
        Clients.showNotification("Todo saved");
    }

    //when user clicks the update button
    @Listen("onClick = #reloadSelectedTodo")
    public void doReloadClick() {
        refreshDetailView();
    }
}
