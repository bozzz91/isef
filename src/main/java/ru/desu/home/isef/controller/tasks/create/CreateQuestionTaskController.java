package ru.desu.home.isef.controller.tasks.create;

import lombok.extern.java.Log;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.Answer;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Question;
import ru.desu.home.isef.entity.Task;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CreateQuestionTaskController extends AbstractVariableCostTaskController {
    
    //wire components
    protected @Wire("#taskPropertyGrid #curTaskQuestion_1")   Textbox curTaskQuestion;
    protected @Wire("#taskPropertyGrid #curTaskAnswer_1")     Textbox curTaskAnswer;
    protected @Wire("#taskPropertyGrid #curTaskAnswer1_1")    Textbox curTaskAnswer1;
    protected @Wire("#taskPropertyGrid #curTaskAnswer2_1")    Textbox curTaskAnswer2;

	protected @Wire Combobox period;

	//data for the view
	protected ListModelList<String> periodList;

	private int questionCount = 1;
	private double additionalCost;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        setVisible(curTaskRemark.getParent().getParent(), false);
		setVisible(curTaskDate.getParent().getParent(), false);
		setVisible(curTaskConfirm.getParent().getParent(), false);

		periodList = new ListModelList<>(config.getAllPeriods());
		periodList.addToSelection(config.getFirstPeriod());
		period.setModel(periodList);

		additionalCost = config.getAdditionalQuestionCost();
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

			t.getQuestions().add(question);
			question.setTask(t);
        } else if (curTaskType.isTest()) {
			for (int i = 1; i <= questionCount; i++) {
				Textbox cmp1 = (Textbox) questionRow.getFellow("curTaskQuestion_" + i, true);
				Textbox cmp2 = (Textbox) questionRow.getFellow("curTaskAnswer_" + i, true);
				Textbox cmp3 = (Textbox) questionRow.getFellow("curTaskAnswer1_" + i, true);
				Textbox cmp4 = (Textbox) questionRow.getFellow("curTaskAnswer2_" + i, true);

				Question question = new Question();
				question.setText(cmp1.getValue());

				Answer wrongAns = new Answer();
				wrongAns.setText(cmp3.getValue());
				question.getAnswers().add(wrongAns);
				wrongAns.setQuestion(question);

				wrongAns = new Answer();
				wrongAns.setText(cmp4.getValue());
				question.getAnswers().add(wrongAns);
				wrongAns.setQuestion(question);

				Answer correct = new Answer();
				correct.setText(cmp2.getValue());
				correct.setCorrect(true);
				question.getAnswers().add(correct);
				correct.setQuestion(question);

				t.getQuestions().add(question);
				question.setTask(t);
			}
		}

		if (curTaskType.isTest() || curTaskType.isQuestion()) {
			t.setVip(vip.isChecked());
			t.setUniqueIp(config.getUniqueIp(uniqueIp.getValue()));
			t.setPeriod(config.getPeriod(period.getValue()));
			t.setSex(config.getSex(sex.getValue()));
		}

		p.setCash(p.getCash() - t.getCost());
        t = taskService.saveTaskAndPerson(t, p);
        authService.getUserCredential().setPerson(p);
        personCashLabel.setValue("Ваш баланс: " + p.getCash());

		return t;
    }

	@Listen("onClick = #taskPropertyGrid #addQuestion")
	public void addAdditionalQuestion() {
		if (++questionCount > 5) {
			return;
		}

		Row newRow = (Row) questionRow.clone();
		newRow.setId(null);
		Component comp1 = newRow.getFellow("curTaskQuestion_1", true);
		comp1.setId(comp1.getId().replace("_1", "_" + questionCount));
		comp1 = newRow.getFellow("curTaskAnswer_1", true);
		comp1.setId(comp1.getId().replace("_1", "_" + questionCount));
		comp1 = newRow.getFellow("curTaskAnswer1_1", true);
		comp1.setId(comp1.getId().replace("_1", "_"+questionCount));
		comp1 = newRow.getFellow("curTaskAnswer2_1", true);
		comp1.setId(comp1.getId().replace("_1", "_"+questionCount));
		questionRow.getParent().appendChild(newRow);
		questionRow.getParent().removeChild(addQuestion.getParent().getParent());
		if (questionCount < 5) {
			questionRow.getParent().appendChild(addQuestion.getParent().getParent());
		}
	}

	protected Double calcMultiplier() {
		double mult = super.calcMultiplier();
		if (curTaskType.isTest()) {
			mult += (questionCount - 1) * additionalCost;
		}
		return mult;
	}

	@Override
	protected String getConfirmMessage() {
		return "";
	}

	@Override
	protected boolean checkIndividualTask() {
		for (int i = 1; i <= questionCount; i++) {
			Textbox cmp1 = (Textbox) questionRow.getFellow("curTaskQuestion_"+i, true);
			Textbox cmp2 = (Textbox) questionRow.getFellow("curTaskAnswer_"+i, true);
			Textbox cmp3 = (Textbox) questionRow.getFellow("curTaskAnswer1_"+i, true);
			Textbox cmp4 = (Textbox) questionRow.getFellow("curTaskAnswer2_"+i, true);
			if (Strings.isBlank(cmp1.getValue())) {
				Clients.showNotification("Укажите контрольный вопрос", "warning", cmp1, "before_start", 5000);
				return false;
			}
			if (Strings.isBlank(cmp2.getValue())) {
				Clients.showNotification("Укажите правильный ответ", "warning", cmp2, "before_start", 5000);
				return false;
			}
			if (Strings.isBlank(cmp3.getValue()) || Strings.isBlank(cmp4.getValue())) {
				Clients.showNotification("Укажите дополнительные неверные ответы", "warning", cmp3, "before_start", 5000);
				return false;
			}
		}
		return true;
	}
}
