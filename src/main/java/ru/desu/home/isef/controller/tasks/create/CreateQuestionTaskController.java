package ru.desu.home.isef.controller.tasks.create;

import lombok.extern.java.Log;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Answer;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Question;
import ru.desu.home.isef.entity.Task;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CreateQuestionTaskController extends AbstractVariableCostTaskController {
    
    //wire components
    protected @Wire("#taskPropertyGrid #curTaskQuestion")   Textbox curTaskQuestion;
    protected @Wire("#taskPropertyGrid #curTaskAnswer")     Textbox curTaskAnswer;
    protected @Wire("#taskPropertyGrid #curTaskAnswer1")    Textbox curTaskAnswer1;
    protected @Wire("#taskPropertyGrid #curTaskAnswer2")    Textbox curTaskAnswer2;
    protected @Wire("#taskPropertyGrid #questionRow")       Row questionRow;

	protected @Wire Combobox period;

	//data for the view
	protected ListModelList<String> periodList;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        setVisible(curTaskRemark.getParent().getParent(), false);
		setVisible(curTaskDate.getParent().getParent(), false);
		setVisible(curTaskConfirm.getParent().getParent(), false);

		periodList = new ListModelList<>(config.getAllPeriods());
		periodList.addToSelection(config.getFirstPeriod());
		period.setModel(periodList);
    }
    
    @Override
    public Task doCreateTask(Person p) {
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
        t.setConfirmation("Верный ответ на вопрос");
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

			t.setVip(vip.isChecked());
			t.setUniqueIp(config.getUniqueIp(uniqueIp.getValue()));
			t.setPeriod(config.getPeriod(period.getValue()));
			t.setSex(config.getSex(sex.getValue()));
        }
        
        t = taskService.saveTaskAndPerson(t, p);
        authService.getUserCredential().setPerson(p);
        personCashLabel.setValue("Ваш баланс: " + p.getCash());

		return t;
    }

	@Override
	protected String getConfirmMessage() {
		return "";
	}

	@Override
	protected boolean checkIndividualTask() {
		if (Strings.isBlank(curTaskQuestion.getValue())) {
			Clients.showNotification("Укажите контрольный вопрос", "warning", curTaskQuestion, "before_start", 5000);
			return false;
		}
		if (Strings.isBlank(curTaskAnswer.getValue())) {
			Clients.showNotification("Укажите правильный ответ", "warning", curTaskAnswer, "before_start", 5000);
			return false;
		}
		if (Strings.isBlank(curTaskAnswer1.getValue()) || Strings.isBlank(curTaskAnswer2.getValue())) {
			Clients.showNotification("Укажите дополнительные неверные ответы", "warning", curTaskAnswer1, "before_start", 5000);
			return false;
		}
		return true;
	}
}
