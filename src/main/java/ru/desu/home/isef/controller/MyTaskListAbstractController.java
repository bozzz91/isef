package ru.desu.home.isef.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.Answer;
import ru.desu.home.isef.entity.Country;
import ru.desu.home.isef.entity.Question;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.services.*;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.utils.FormatUtil;

import java.text.SimpleDateFormat;
import java.util.List;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public abstract class MyTaskListAbstractController extends SelectorComposer<Component> {

    //wire components
    protected @Wire Listbox taskList;
    protected @Wire East curTaskEastBlock;
    protected @Wire("#taskPropertyGrid #curTaskDescription")    Textbox curTaskDescription;
    protected @Wire("#taskPropertyGrid #curTaskConfirm")        Textbox curTaskConfirm;
    protected @Wire("#taskPropertyGrid #taskLink")              Textbox taskLink;
    protected @Wire("#taskPropertyGrid #curTaskSubjectEdit")    Textbox curTaskSubjectEdit;
    protected @Wire("#taskPropertyGrid #curTaskDate")           Label curTaskDate;
	protected @Wire("#taskPropertyGrid #labelTaskType")         Label labelTaskType;
	protected @Wire("#taskPropertyGrid #country")               Listbox country;
    
    protected @Wire("#taskPropertyGrid #curTaskQuestion_1")   Textbox curTaskQuestion;
    protected @Wire("#taskPropertyGrid #curTaskAnswer_1")     Textbox curTaskAnswer;
    protected @Wire("#taskPropertyGrid #curTaskAnswer1_1")    Textbox curTaskAnswer1;
    protected @Wire("#taskPropertyGrid #curTaskAnswer2_1")    Textbox curTaskAnswer2;
	protected @Wire("#taskPropertyGrid #questionRow")         Row questionRow;
	protected @Wire("#taskPropertyGrid #addQuestion")         Button addQuestion;
    
    //live data model
    protected ListModelList<Task> taskListModel;

	//cur task
	protected Task curTask;
    
    //services
    protected @WireVariable TaskService taskService;
    protected @WireVariable AuthenticationService authService;
    protected @WireVariable PersonService personService;
    protected @WireVariable TaskTypeService taskTypeService;
	protected @WireVariable BanService banService;
	protected @WireVariable CountryService countryService;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
		initModel();

		addQuestion.setVisible(false);
		List<Country> countries = countryService.findAll();
		ListModelList<Country> model = new ListModelList<>(countries);
		model.setMultiple(true);
		country.setModel(model);
		country.setCheckmark(false);
		country.setMultiple(true);
    }

	protected abstract void initModel();

    protected void refreshDetailView() {
        //refresh the detail view of selected task
        if (curTask == null) {
            //clean
            curTaskEastBlock.setOpen(false);
            curTaskEastBlock.setVisible(false);
            curTaskSubjectEdit.setValue(null);
            curTaskDate.setValue(null);
            curTaskDescription.setValue(null);
			country.clearSelection();
            labelTaskType.setValue(null);
            taskLink.setValue(null);
            curTaskConfirm.setValue(null);
            
            //questions
            curTaskQuestion.setValue(null);
            curTaskAnswer.setValue(null);
            curTaskAnswer1.setValue(null);
            curTaskAnswer2.setValue(null);
            questionRow.setVisible(false);
        } else {
            curTaskEastBlock.setVisible(true);
            curTaskEastBlock.setOpen(true);
            curTaskSubjectEdit.setValue(curTask.getSubject());
            curTaskDate.setValue(new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(curTask.getCreationTime()));
            curTaskDescription.setValue(curTask.getDescription());
			country.setModel(new ListModelSet<>(curTask.getCountries()));
            labelTaskType.setValue(curTask.getTaskType()
                    +" (Кол-во просмотров: "+curTask.getCount()
                    +", стоимость: "+ FormatUtil.formatDouble(curTask.getCost())
                    +", за клик: "+curTask.getTaskType().getGift()+")");
            String link = curTask.getLink();
            int idx_1 = link.indexOf("//")+2;
            int idx_2 = link.indexOf("/", 9) != -1 ? link.indexOf("/", 9) : link.length();
            taskLink.setValue(link.substring(idx_1, idx_2));
            curTaskConfirm.setValue(curTask.getConfirmation());
            
            //questions
            if (curTask.getTaskType().isQuestion()) {
				questionRow.setVisible(true);
				if (!curTask.getQuestions().isEmpty()) {
					Question quest = curTask.getQuestions().iterator().next();
					curTaskQuestion.setValue(quest.getText());
					Answer correct = quest.getCorrectAnswer();
					curTaskAnswer.setValue(correct == null ? null : correct.getText());
					int wrongAnsCount = 0;
					for (Answer ans : quest.getAnswers()) {
						if (!ans.isCorrect()) {
							if (wrongAnsCount == 0) {
								curTaskAnswer1.setValue(ans.getText());
								wrongAnsCount++;
							} else if (wrongAnsCount == 1) {
								curTaskAnswer2.setValue(ans.getText());
								wrongAnsCount++;
							} else {
								break;
							}
						}
					}
				}
			} else if (curTask.getTaskType().isTest()) {
				// TODO show all questions
				questionRow.setVisible(false);
            } else {
                questionRow.setVisible(false);
            }
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
    
    protected void doneTask(Task t, boolean msg) {
        taskService.done(t);
        if (msg) {
			Clients.showNotification("Задание выполнено", "info", null, "middle_center", 2000, true);
		}
    }
    
    protected void doneTask(Task t) {
        doneTask(t, true);
    }
}
