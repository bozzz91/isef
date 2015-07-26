package ru.desu.home.isef.controller;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListOnDraftController extends MyTaskListAbstractController {
    private static final long serialVersionUID = 1L;

    //wire components
    @Wire Combobox taskTypeList;
    //@Wire Textbox taskSubject;
    @Wire Button addTask, updateTask;
    @Wire("#taskPropertyGrid #curTaskRemark") Textbox curTaskRemark;
    @Wire("#taskPropertyGrid #rowRemark") Row rowRemark;
    
    //data for the view
    protected ListModelList<TaskType> taskTypesModel;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        List<TaskType> types = taskTypeService.findAll();
        taskTypesModel = new ListModelList<>(types);
        taskTypesModel.addToSelection(types.get(0));
        taskTypeList.setModel(taskTypesModel);
        
        Person p = authService.getUserCredential().getPerson();
        p = personService.findById(p.getId());
        List<Task> todoList = taskService.getTasksByOwnerAndStatus(p, Status._1_DRAFT);
        taskListModel = new ListModelList<>(todoList);
        taskList.setModel(taskListModel);
    }

	/* UNUSED перенесено на форму создания заданий*/
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

        Map<String, String> params = new HashMap<>();
        params.put("width", "600");
        Messagebox.show(msg.toString(),
				"Подтверждение публикации",
				new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.CANCEL},
				new String[] {"Всё верно", "Отмена"},
				Messagebox.QUESTION,
				Messagebox.Button.OK,
				new EventListener<Messagebox.ClickEvent>() {
					@Override
					public void onEvent(Messagebox.ClickEvent event) throws Exception {
						if (event.getName().equals(Messagebox.ON_YES)) {
							int index = taskListModel.indexOf(curTask);
							String link = taskLink.getValue();
							if (!link.startsWith("http://") && !link.startsWith("https://")) {
								link = "http://" + link;
							}
							List<Ban> bans = banService.find(link);
							if (bans != null && !bans.isEmpty()) {
								Clients.showNotification("Сайт " + link + " занесен в черный список", "warning", null, "middle_center", 5000);
								return;
							}

							if (curTask.getTaskType().isQuestion() || curTask.getTaskType().isSurfing()) {
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

							curTask = null;
							refreshDetailView();

							//show message for user
							Clients.showNotification("Задание сохранено и опубликовано", "info", null, "middle_center", 5000);
						}
					}
				}, params);
    }

    @Listen("onClick = #addTask")
    public void openCreateTaskWindow() {
        int index = taskTypeList.getSelectedIndex();
        if (index == -1) {
            Clients.showNotification("Выберите тип задания", "warning", taskTypeList, "after_pointer", 3000);
            return;
        }
        TaskType selectedType = taskTypeList.<TaskType>getModel().getElementAt(index);
		Map<Object, Object> args = new HashMap<>();
		args.put("taskType", selectedType);
        Window createTaskWin = (Window)Executions.createComponents(selectedType.getCreateTemplate(), null, args);
        createTaskWin.setPosition("center,center");
        createTaskWin.setDraggable("false");
		/* Сразу публикуем, без отображения в списке задач */
		/*if (!createTaskWin.getEventListeners(Events.ON_CLOSE).iterator().hasNext()) {
			createTaskWin.addEventListener(Events.ON_CLOSE, new SerializableEventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					if (event.getData() != null) {
						Task createdTask = (Task) event.getData();
						//update the model of listbox
						taskListModel.add(createdTask);
						//set the new selection
						taskListModel.addToSelection(createdTask);
						//refresh detail view
						refreshDetailView();
					}
				}
			});
		}*/
        createTaskWin.doHighlighted();
    }
    
    //when user clicks the delete button of each todo on the list
    @Listen("onTaskDelete = #taskList")
    public void doTaskDelete(ForwardEvent evt) {
        Button btn = (Button) evt.getOrigin().getTarget();
        Listitem item = (Listitem) btn.getParent().getParent();

        final Task todo = item.getValue();

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

                            //personCashLabel.setValue("Ваш баланс: " + p.getCash());
                            //update the model of listbox
                            taskListModel.remove(todo);

                            if (todo.equals(curTask)) {
                                //refresh selected task view
                                curTask = null;
                                refreshDetailView();
                            }
                        }
                    }
                });
    }

    @Override
    @Listen("onSelect = #taskList")
    public void doTaskSelect() {
        if (taskListModel.isSelectionEmpty()) {
            //just in case for the no selection
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

        Clients.showNotification("Задание сохранено");
    }
}
