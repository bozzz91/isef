package ru.desu.home.isef.controller;

import java.text.SimpleDateFormat;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
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
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Answer;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.TaskTypeService;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public abstract class MyTaskListAbstractController extends SelectorComposer<Component> {
    private static final String CURRENT_TASK_ATTRIBUTE = "curTask";
    
    //wire components
    protected @Wire Listbox taskList;
    protected @Wire East curTaskEastBlock;
    protected @Wire("#taskPropertyGrid #curTaskDescription")    Textbox curTaskDescription;
    protected @Wire("#taskPropertyGrid #curTaskConfirm")        Textbox curTaskConfirm;
    protected @Wire("#taskPropertyGrid #taskLink")              Textbox taskLink;
    protected @Wire("#taskPropertyGrid #curTaskSubjectEdit")    Textbox curTaskSubjectEdit;
    protected @Wire("#taskPropertyGrid #curTaskDate")           Label curTaskDate;
    protected @Wire("#taskPropertyGrid #labelTaskType")         Label labelTaskType;
    
    protected @Wire("#taskPropertyGrid #curTaskQuestion")   Textbox curTaskQuestion;
    protected @Wire("#taskPropertyGrid #curTaskAnswer")     Textbox curTaskAnswer;
    protected @Wire("#taskPropertyGrid #curTaskAnswer1")    Textbox curTaskAnswer1;
    protected @Wire("#taskPropertyGrid #curTaskAnswer2")    Textbox curTaskAnswer2;
    protected @Wire("#taskPropertyGrid #questionRow")       Row questionRow;
    
    protected @Wire Button closeTask;
    
    //live data model
    protected ListModelList<Task> taskListModel;
    
    //services
    protected @WireVariable TaskService taskService;
    protected @WireVariable AuthenticationService authService;
    protected @WireVariable PersonService personService;
    protected @WireVariable TaskTypeService taskTypeService;
    
    protected Task getCurTask() {
        Session sess = Sessions.getCurrent();
        return (Task)sess.getAttribute(CURRENT_TASK_ATTRIBUTE);
    }
    
    protected void setCurTask(Task task) {
        Session sess = Sessions.getCurrent();
        sess.setAttribute(CURRENT_TASK_ATTRIBUTE, task);
    }
    
    protected void removeCurTask() {
        Session sess = Sessions.getCurrent();
        sess.removeAttribute(CURRENT_TASK_ATTRIBUTE);
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        removeCurTask();
    }

    protected void refreshDetailView() {
        //refresh the detail view of selected todo
        Task curTask = getCurTask();
        if (curTask == null) {
            //clean
            curTaskEastBlock.setOpen(false);
            curTaskEastBlock.setVisible(false);
            curTaskSubjectEdit.setValue(null);
            curTaskDate.setValue(null);
            curTaskDescription.setValue(null);
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
            curTaskDate.setValue(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(curTask.getCreationTime()));
            curTaskDescription.setValue(curTask.getDescription());
            labelTaskType.setValue(curTask.getTaskType()
                    +" (Кликов: "+curTask.getCount()
                    +", стоимость: "+curTask.getCost()
                    +", за клик: "+curTask.getTaskType().getGift()+")");
            String link = curTask.getLink();
            int idx_1 = link.indexOf("//")+2;
            int idx_2 = link.indexOf("/", 9) != -1 ? link.indexOf("/", 9) : link.length();
            taskLink.setValue(link.substring(idx_1, idx_2));
            curTaskConfirm.setValue(curTask.getConfirmation());
            
            //questions
            if (curTask.getTaskType().isQuestion()) {
                questionRow.setVisible(true);
                if (curTask.getQuestion() != null) {
                    curTaskQuestion.setValue(curTask.getQuestion().getText());
                    Answer correct = curTask.getQuestion().getCorrectAnswer();
                    curTaskAnswer.setValue(correct == null ? null : correct.getText());
                    int wrongAnsCount = 0;
                    for (Answer ans : curTask.getQuestion().getAnswers()) {
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
            } else {
                questionRow.setVisible(false);
            }
        }
    }
    
    @Listen("onClick = #closeTask")
    public void closeSelectedTaskClick() {
        Task curTask = getCurTask();
        taskListModel.removeFromSelection(curTask);
        removeCurTask();
        refreshDetailView();
    }
    
    @Listen("onSelect = #taskList")
    public void doTaskSelect() {
        Task curTask;
        if (taskListModel.isSelectionEmpty()) {
            //just in case for the no selection
            curTask = null;
        } else {
            curTask = taskListModel.getSelection().iterator().next();
        }
        setCurTask(curTask);
        refreshDetailView();
    }
    
    protected void doneTask(Task t, boolean msg) {
        taskService.done(t);
        if (msg)
            Clients.showNotification("Задание выполнено", "info", null, "middle_center", 2000, true);
    }
    
    protected void doneTask(Task t) {
        doneTask(t, true);
    }
}
