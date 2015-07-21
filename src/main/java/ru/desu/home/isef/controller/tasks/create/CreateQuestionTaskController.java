package ru.desu.home.isef.controller.tasks.create;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Answer;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Question;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.utils.SessionUtil;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CreateQuestionTaskController extends AbstractCreateTaskController {
    
    //wire components
    protected @Wire("#taskPropertyGrid #curTaskRemark")     Textbox curTaskRemark;
    protected @Wire("#taskPropertyGrid #curTaskConfirm")    Textbox curTaskConfirm;
    protected @Wire("#taskPropertyGrid #taskLink")          Textbox taskLink;
    protected @Wire("#taskPropertyGrid #curTaskDate")       Label curTaskDate;
    
    protected @Wire("#taskPropertyGrid #curTaskQuestion")   Textbox curTaskQuestion;
    protected @Wire("#taskPropertyGrid #curTaskAnswer")     Textbox curTaskAnswer;
    protected @Wire("#taskPropertyGrid #curTaskAnswer1")    Textbox curTaskAnswer1;
    protected @Wire("#taskPropertyGrid #curTaskAnswer2")    Textbox curTaskAnswer2;
    protected @Wire("#taskPropertyGrid #questionRow")       Row questionRow;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        setVisible(curTaskRemark, false);
    }
    
    @Override
    public void doCreateTask() {
        if (countSpin.getValue() != null && countSpin.getValue() <= 0) {
            Clients.showNotification("Задано неверное кол-во кликов/переходов", "error", countSpin, "after_end", 3000);
            return;
        }

        Person p = authService.getUserCredential().getPerson();

        if (p.getCash() < cost) {
            Clients.showNotification("Недостаточно средств на вашем балансе, чтобы создать столько кликов", "warning", countSpin, "after_end", 3000);
            return;
        }
  
        String subject = curTaskSubjectEdit.getValue();
        String link = taskLink.getValue();
        if (!link.startsWith("http://") && !link.startsWith("https://")) {
            link = "http://" + link;
        }

        Task t = new Task();
        t.setSubject(subject);
        t.setTaskType(curTaskType);
        t.setCount(countSpin.getValue());
        t.setCost(cost);
        t.setDescription(curTaskDescription.getValue());
        t.setLink(link);
        t.setConfirmation(curTaskConfirm.getValue());
        t.setOwner(authService.getUserCredential().getPerson());

        p.setCash(p.getCash() - t.getCost());
        
        if (curTaskType.isQuestion()) {
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
        }
        
        Task curTask = taskService.saveTaskAndPerson(t, p);
        SessionUtil.setCurTask(curTask);
        SessionUtil.removeCurTaskType();
        authService.getUserCredential().setPerson(p);
        personCashLabel.setValue("Ваш баланс: " + p.getCash());
        
        Events.postEvent(new Event(Events.ON_CLOSE , createTaskWin, t));
        
        createTaskWin.detach();
    }
    
    @Override
    protected Double calcCost(Double multi, Integer count) {
        return multi * count;
    }
}
