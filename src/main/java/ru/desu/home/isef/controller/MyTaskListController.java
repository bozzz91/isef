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
import org.zkoss.zul.Row;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Status;
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
    Textbox taskSubject, resultCost;
    @Wire
    Button addTodo, updateTask;
    @Wire
    Listbox taskList;
    @Wire
    Combobox taskTypeList;
    @Wire
    Row rowRemark;
    @Wire
    Spinner countSpin;

    @Wire
    East curTaskEastBlock;
    @Wire
    Textbox curTaskSubject, curTaskDescription, curTaskLink, curTaskConfirm, curTaskRemark;
    @Wire
    Label curTaskDate, labelTaskType, personCashLabel;

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
    ListModelList<Task> taskListModel;
    Task curTask;
    Double cost;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksByOwner(p);
        taskListModel = new ListModelList<>(todoList);
        taskList.setModel(taskListModel);

        List<TaskType> types = taskTypeService.findAll();
        taskTypesModel = new ListModelList<>(types);
        taskTypesModel.addToSelection(types.get(0));
        taskTypeList.setModel(taskTypesModel);

        personCashLabel.setValue("Ваш баланс: " + p.getCash());
    }

    @Listen("onClick = #closeTask")
    public void closeSelectedTaskClick() {
        taskListModel.removeFromSelection(curTask);
        curTask = null;
        refreshDetailView();
    }

    @Listen("onClick = #publishTask")
    public void publishTask() {
        if (Strings.isBlank(curTaskSubject.getValue())) {
            Clients.showNotification("Введите название задания", "warning", curTaskSubject, "after_end", 3000);
            return;
        }
        if (Strings.isBlank(curTaskDescription.getValue())) {
            Clients.showNotification("Напишите что необходимо сделать в вашем задании", "warning", curTaskDescription, "before_start", 5000);
            return;
        }
        if (Strings.isBlank(curTaskLink.getValue())) {
            Clients.showNotification("Укажите ссылку для перехода", "warning", curTaskLink, "before_start", 5000);
            return;
        }

        StringBuilder msg = new StringBuilder("Публикация задания. Убедитесь, что все данные введены корректно");
        msg.append("\nдля успешного прохождения модерации.\n\"").append("Если у модератора возникнут претензии к введенным значениям,\n");
        msg.append("то он может вернуть его Вам для редактирования с указанием своих примечаний");
        Messagebox.show(msg.toString(),
                "Подтверждение публикации",
                Messagebox.YES | Messagebox.CANCEL,
                Messagebox.QUESTION,
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        if (event.getName().equals(Messagebox.ON_YES)) {
                            int index = taskListModel.indexOf(curTask);

                            curTask.setStatus(Status._2_MODER);
                            curTask.setSubject(curTaskSubject.getValue());
                            curTask.setLink(curTaskLink.getValue());
                            curTask.setConfirmation(curTaskConfirm.getValue());
                            curTask.setDescription(curTaskDescription.getValue());
                            curTask.setRemark(null);
                            //selectedTodo.setPriority(priorityListModel.getSelection().iterator().next());

                            //save data and get updated Todo object
                            curTask = taskService.save(curTask);

                            //replace original Todo object in listmodel with updated one
                            taskListModel.remove(index);

                            curTask = null;
                            refreshDetailView();

                            //show message for user
                            Clients.showNotification("Задание сохранено и опубликовано", "info", null, "middle_center", 5000);
                        }
                    }
                });
    }
    
    @Listen("onChange = #countSpin")
    public void onChangeClickCount() {
        int index = taskTypeList.getSelectedIndex();
        if (index == -1) {
            Clients.showNotification("Выберите тип задания", "warning", taskTypeList, "after_end", 3000);
            return;
        }
        TaskType selectedType = taskTypeList.<TaskType>getModel().getElementAt(index);
        double multiplier = selectedType.getMultiplier();
        cost = multiplier*countSpin.getValue();
        resultCost.setValue("Стоимость : "+cost);
    }

    //when user clicks on the button or enters on the textbox
    @Listen("onClick = #addTodo; onOK = #taskSubject")
    public void doTodoAdd() {
        if (taskTypeList.getSelectedIndex() == -1) {
            Clients.showNotification("Выберите тип задания", "warning", taskTypeList, "after_end", 3000);
            return;
        }
        
        Person p = authService.getUserCredential().getPerson();
        
        if (p.getCash() < cost) {
            Clients.showNotification("Недостаточно средств на вашем балансе чтобы создать задачу выбранного типа", "warning", taskTypeList, "after_end", 3000);
            return;
        }

        //get user input from view
        String subject = taskSubject.getValue();
        if (Strings.isBlank(subject)) {
            Clients.showNotification("Придумайте название", "warning", taskSubject, "after_pointer", 3000);
        } else {
            int index = taskTypeList.getSelectedIndex();
            TaskType selectedType = taskTypeList.<TaskType>getModel().getElementAt(index);
            
            Task t = new Task();
            t.setSubject(subject);
            t.setTaskType(selectedType);
            t.setCount(countSpin.getValue());
            t.setCost(cost);
            t.setDescription("");
            t.setOwner(authService.getUserCredential().getPerson());

            p.setCash(p.getCash() - t.getCost());

            curTask = taskService.saveTaskAndPerson(t, p);
            authService.getUserCredential().setPerson(p);
            personCashLabel.setValue("Ваш баланс: " + p.getCash());

            //update the model of listbox
            taskListModel.add(curTask);
            //set the new selection
            taskListModel.addToSelection(curTask);

            //refresh detail view
            refreshDetailView();

            //reset value for fast typing.
            taskSubject.setValue("");
        }
    }

    //when user checks on the checkbox of each todo on the list
    @Listen("onTaskCheck = #taskList")
    public void doTodoCheck(ForwardEvent evt) {
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

    //when user clicks the delete button of each todo on the list
    @Listen("onTaskDelete = #taskList")
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
                            double cost = todo.getCost();

                            //delete data
                            taskService.delete(todo);

                            Person p = authService.getUserCredential().getPerson();
                            p = personService.findById(p.getId());
                            p.setCash(p.getCash() + cost);
                            personService.save(p);
                            authService.getUserCredential().setPerson(p);

                            personCashLabel.setValue("Ваш баланс: " + p.getCash());
                            //update the model of listbox
                            taskListModel.remove(todo);

                            if (todo.equals(curTask)) {
                                //refresh selected todo view
                                curTask = null;
                                refreshDetailView();
                            }
                        }
                    }
                });
    }

    //when user selects a todo of the listbox
    @Listen("onSelect = #taskList")
    public void doTodoSelect() {
        if (taskListModel.isSelectionEmpty()) {
            //just in case for the no selection
            curTask = null;
        } else {
            curTask = taskListModel.getSelection().iterator().next();
            rowRemark.setVisible(false);
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
            curTaskLink.setValue(null);
            curTaskConfirm.setValue(null);
            curTaskDate.setValue(null);
            labelTaskType.setValue(null);
            curTaskDescription.setValue(null);
            updateTask.setDisabled(true);
            curTaskRemark.setValue(null);
            rowRemark.setVisible(false);
        } else {
            curTaskEastBlock.setVisible(true);
            curTaskEastBlock.setOpen(true);
            curTaskSubject.setValue(curTask.getSubject());
            curTaskLink.setValue(curTask.getLink());
            curTaskConfirm.setValue(curTask.getConfirmation());
            curTaskDate.setValue(curTask.getCreationTime().toString());
            labelTaskType.setValue(curTask.getTaskType().toString());
            curTaskDescription.setValue(curTask.getDescription());
            updateTask.setDisabled(false);
            if (!Strings.isBlank(curTask.getRemark())) {
                curTaskRemark.setValue(curTask.getRemark());
                rowRemark.setVisible(true);
            }
        }
    }

    //when user clicks the update button
    @Listen("onClick = #updateTask")
    public void doUpdateClick() {
        if (Strings.isBlank(curTaskSubject.getValue())) {
            Clients.showNotification("Введите название задания", "warning", curTaskSubject, "after_end", 3000);
            return;
        }
        int index = taskListModel.indexOf(curTask);

        curTask.setSubject(curTaskSubject.getValue());
        curTask.setLink(curTaskLink.getValue());
        curTask.setConfirmation(curTaskConfirm.getValue());
        curTask.setDescription(curTaskDescription.getValue());

        curTask = taskService.save(curTask);
        taskListModel.set(index, curTask);

        Clients.showNotification("Задание сохранено");
    }

    //when user clicks the update button
    @Listen("onClick = #reloadTask")
    public void doReloadClick() {
        refreshDetailView();
    }
}
