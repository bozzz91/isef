package ru.desu.home.isef.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Answer;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Question;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.entity.TaskType;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListOnDraftController extends MyTaskListAbstractController {
    private static final long serialVersionUID = 1L;

    //wire components
    @Wire Textbox taskSubject, resultCost;
    @Wire Button addTask, updateTask;
    @Wire Combobox taskTypeList;
    @Wire Spinner countSpin;
    @Wire Label personCashLabel;
    @Wire("#taskPropertyGrid #curTaskRemark") Textbox curTaskRemark;
    @Wire("#taskPropertyGrid #rowRemark") Row rowRemark;
    

    //data for the view
    ListModelList<TaskType> taskTypesModel;
    Double cost;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        Person p = authService.getUserCredential().getPerson();
        p = personService.findById(p.getId());
        List<Task> todoList = taskService.getTasksByOwnerAndStatus(p, Status._1_DRAFT);
        taskListModel = new ListModelList<>(todoList);
        taskList.setModel(taskListModel);

        List<TaskType> types = taskTypeService.findAll();
        taskTypesModel = new ListModelList<>(types);
        taskTypesModel.addToSelection(types.get(0));
        taskTypeList.setModel(taskTypesModel);

        personCashLabel.setValue("Ваш баланс: " + p.getCash());

        cost = calcCost(types.get(0).getMultiplier(), countSpin.getValue());
        resultCost.setValue("Стоимость : " + cost);
    }

    @Listen("onChange = #taskTypeList")
    public void onChangeType() {
        cost = calcCost(taskTypeList.getSelectedItem().<TaskType>getValue().getMultiplier(), countSpin.getValue());
        resultCost.setValue("Стоимость : " + cost);
    }

    @Listen("onClick = #publishTask")
    public void publishTask() {
        if (Strings.isBlank(curTaskSubjectEdit.getValue())) {
            Clients.showNotification("Введите название задания", "warning", curTaskSubjectEdit, "after_end", 3000);
            return;
        }
        if (Strings.isBlank(curTaskDescription.getValue())) {
            Clients.showNotification("Напишите что необходимо сделать в вашем задании", "warning", curTaskDescription, "before_start", 5000);
            return;
        }
        if (Strings.isBlank(taskLink.getValue())) {
            Clients.showNotification("Укажите ссылку для перехода", "warning", taskLink, "before_start", 5000);
            return;
        }
        
        StringBuilder msg = new StringBuilder("Публикация задания. Убедитесь, что все данные введены корректно");
        
        Task curTask = getCurTask();
        if (curTask.getTaskType().isQuestion()) {
            if (Strings.isBlank(curTaskQuestion.getValue())) {
                Clients.showNotification("Укажите контрольный вопрос", "warning", curTaskQuestion, "before_start", 5000);
                return;
            }
            if (Strings.isBlank(curTaskAnswer.getValue())) {
                Clients.showNotification("Укажите правильный ответ", "warning", curTaskAnswer, "before_start", 5000);
                return;
            }
            if (Strings.isBlank(curTaskAnswer1.getValue()) || Strings.isBlank(curTaskAnswer2.getValue())) {
                Clients.showNotification("Укажите дополнительные неверные ответы", "warning", curTaskAnswer1, "before_start", 5000);
                return;
            }
            msg.append(".");
        } else {
            msg.append(" для успешного прохождения этапа модерации.").append("\n\nЕсли у модератора возникнут претензии к введенным значениям,");
            msg.append(" то он может вернуть его Вам на корректирование с указанием своих примечаний");
        }

        Map params = new HashMap();
        params.put("width", 600);
        Messagebox.show(msg.toString(),
                "Подтверждение публикации",
                new Messagebox.Button[]{Messagebox.Button.YES, Messagebox.Button.CANCEL},
                new String[]{"Всё верно", "Отмена"},
                Messagebox.QUESTION,
                Messagebox.Button.OK,
                new EventListener() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        if (event.getName().equals(Messagebox.ON_YES)) {
                            Task curTask = getCurTask();
                            int index = taskListModel.indexOf(curTask);
                            String link = taskLink.getValue();
                            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                                link = "http://" + link;
                            }

                            if (curTask.getTaskType().isQuestion()) {
                                curTask.setStatus(Status._3_PUBLISH);
                            } else {
                                curTask.setStatus(Status._2_MODER);
                            }
                            
                            curTask.setSubject(curTaskSubjectEdit.getValue());
                            curTask.setLink(link);
                            curTask.setConfirmation(curTaskConfirm.getValue());
                            curTask.setDescription(curTaskDescription.getValue());
                            curTask.setRemark(null);
                            //selectedTodo.setPriority(priorityListModel.getSelection().iterator().next());

                            //save data and get updated Todo object
                            taskService.save(curTask);

                            //replace original Todo object in listmodel with updated one
                            taskListModel.remove(index);

                            removeCurTask();
                            refreshDetailView();

                            //show message for user
                            Clients.showNotification("Задание сохранено и опубликовано", "info", null, "middle_center", 5000);
                        }
                    }
                }, params);
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
        cost = calcCost(multiplier, countSpin.getValue());
        resultCost.setValue("Стоимость : " + cost);
    }

    private Double calcCost(Double multi, Integer count) {
        return multi * count;
    }

    //when user clicks on the button or enters on the textbox
    @Listen("onClick = #addTask; onOK = #taskSubject")
    public void doTaskAdd() {
        if (taskTypeList.getSelectedIndex() == -1) {
            Clients.showNotification("Выберите тип задания", "warning", taskTypeList, "after_end", 3000);
            return;
        }

        Person p = authService.getUserCredential().getPerson();

        if (p.getCash() < cost) {
            Clients.showNotification("Недостаточно средств на вашем балансе, чтобы создать столько кликов", "warning", countSpin, "after_end", 3000);
            return;
        }

        //get user input from view
        String subject = taskSubject.getValue();
        if (Strings.isBlank(subject)) {
            Clients.showNotification("Придумайте название", "warning", taskSubject, "after_pointer", 3000);
        } else {
            int index = taskTypeList.getSelectedIndex();
            TaskType selectedType = taskTypeList.<TaskType>getModel().getElementAt(index);
            String link = taskLink.getValue();
            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                link = "http://" + link;
            }

            Task t = new Task();
            t.setSubject(subject);
            t.setTaskType(selectedType);
            t.setCount(countSpin.getValue());
            t.setCost(cost);
            t.setDescription("");
            t.setLink(link);
            t.setConfirmation(curTaskConfirm.getValue());
            t.setOwner(authService.getUserCredential().getPerson());
            
            if (selectedType.isQuestion()) {
                Question question = new Question();
                question.setText(curTaskQuestion.getValue());
                
                Answer wrongAns = new Answer();
                wrongAns.setText(curTaskAnswer1.getValue());
                question.getAnswers().add(wrongAns);
                wrongAns.setQuestion(question);
                
                wrongAns = new Answer();
                wrongAns.setText(curTaskAnswer2.getValue());
                question.getAnswers().add(wrongAns);
                wrongAns.setQuestion(question);
                
                Answer correct = new Answer();
                correct.setText(curTaskAnswer.getValue());
                correct.setCorrect(true);
                question.getAnswers().add(correct);
                correct.setQuestion(question);
                
                t.setQuestion(question);
                question.setTask(t);
                //taskService.save(question);
            }

            p.setCash(p.getCash() - t.getCost());

            Task curTask = taskService.saveTaskAndPerson(t, p);
            setCurTask(curTask);
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

    //when user clicks the delete button of each todo on the list
    @Listen("onTaskDelete = #taskList")
    public void doTaskDelete(ForwardEvent evt) {
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

                            Task curTask = getCurTask();
                            if (todo.equals(curTask)) {
                                //refresh selected todo view
                                removeCurTask();
                                refreshDetailView();
                            }
                        }
                    }
                });
    }

    @Override
    @Listen("onSelect = #taskList")
    public void doTaskSelect() {
        Task curTask;
        if (taskListModel.isSelectionEmpty()) {
            //just in case for the no selection
            curTask = null;
        } else {
            curTask = taskListModel.getSelection().iterator().next();
            rowRemark.setVisible(false);
        }
        setCurTask(curTask);
        refreshDetailView();
    }

    @Override
    protected void refreshDetailView() {
        super.refreshDetailView();
        
        Task curTask = getCurTask();
        if (curTask == null) {
            //clean
            curTaskRemark.setValue(null);
            rowRemark.setVisible(false);
            curTaskSubjectEdit.setValue(null);
            taskLink.setValue(null);
        } else {
            curTaskSubjectEdit.setValue(curTask.getSubject());
            taskLink.setValue(curTask.getLink());
            if (!Strings.isBlank(curTask.getRemark())) {
                curTaskRemark.setValue(curTask.getRemark());
                rowRemark.setVisible(true);
            } else {
                rowRemark.setVisible(false);
            }
        }
    }

    //when user clicks the update button
    @Listen("onClick = #updateTask")
    public void doUpdateClick() {
        if (Strings.isBlank(curTaskSubjectEdit.getValue())) {
            Clients.showNotification("Введите название задания", "warning", curTaskSubjectEdit, "after_end", 3000);
            return;
        }
        Task curTask = getCurTask();
        int index = taskListModel.indexOf(curTask);
        String link = taskLink.getValue();
        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            link = "http://" + link;
        }

        curTask.setSubject(curTaskSubjectEdit.getValue());
        curTask.setLink(link);
        curTask.setConfirmation(curTaskConfirm.getValue());
        curTask.setDescription(curTaskDescription.getValue());
        
        if (curTask.getTaskType().isQuestion()) {
            Question question = curTask.getQuestion();
            question.setText(curTaskQuestion.getValue());
            Answer correct = curTask.getQuestion().getCorrectAnswer();
            correct.setText(curTaskAnswer.getValue());
            int wrongAnsCount = 0;
            for (Answer ans : curTask.getQuestion().getAnswers()) {
                if (!ans.isCorrect()) {
                    if (wrongAnsCount == 0) {
                        ans.setText(curTaskAnswer1.getValue());
                        wrongAnsCount++;
                    } else if (wrongAnsCount == 1) {
                        ans.setText(curTaskAnswer2.getValue());
                        wrongAnsCount++;
                    } else {
                        break;
                    }
                }
            }
        }

        curTask = taskService.save(curTask);
        taskListModel.set(index, curTask);
        setCurTask(curTask);

        Clients.showNotification("Задание сохранено");
    }
}
