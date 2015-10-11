package ru.desu.home.isef.controller.tasks.execute;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.*;
import ru.desu.home.isef.services.auth.UserCredential;
import ru.desu.home.isef.utils.SessionUtil;

import java.util.*;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ExecuteTestController extends AbstractExecuteTaskController {

	@Wire Textbox text;
	@Wire Window readTaskWin;
	@Wire Grid answerGrid;
	/*@Wire Label question;
	@Wire Button answer1, answer2, answer3;*/

	private int questionCount = 0;
	private int correctIndexes[];
	private int correctAnswerCount = 0;

	@Override
	public void doAfterCompose(Component component) throws Exception {
		super.doAfterCompose(component);
		text.setValue(task.getDescription());

		correctIndexes = new int[task.getQuestions().size()];
		for (Question quest : task.getQuestions()) {
			if (++questionCount > 1) {
				Row newQuestionRow = (Row) answerGrid.getFellow("questionRow_1", true).clone();
				Row newAnswerRow = (Row) answerGrid.getFellow("answerRow_1", true).clone();

				newQuestionRow.setId(null);
				newAnswerRow.setId(null);

				Component comp = newQuestionRow.getFellow("question_1", true);
				comp.setId(comp.getId().replace("_1", "_" + questionCount));

				comp = newAnswerRow.getFellow("answer1_1", true);
				comp.setId(comp.getId().replace("_1", "_" + questionCount));
				comp = newAnswerRow.getFellow("answer2_1", true);
				comp.setId(comp.getId().replace("_1", "_" + questionCount));
				comp = newAnswerRow.getFellow("answer3_1", true);
				comp.setId(comp.getId().replace("_1", "_" + questionCount));

				Rows rows = (Rows) answerGrid.getFellow("questionRow_1", true).getParent();
				rows.appendChild(newQuestionRow);
				rows.appendChild(newAnswerRow);
				Row cancelButtonRow = (Row) rows.getFellow("cancelButtonRow");
				rows.removeChild(cancelButtonRow);
				rows.appendChild(cancelButtonRow);
			}

			int currentCorrectIndex = new Random().nextInt(3) + 1;
			correctIndexes[questionCount-1] = currentCorrectIndex;
			((Label)answerGrid.getFellow("question_"+questionCount, true)).setValue(quest.getText());
			Button answer1 = (Button) answerGrid.getFellow("answer1_"+questionCount, true);
			Button answer2 = (Button) answerGrid.getFellow("answer2_"+questionCount, true);
			Button answer3 = (Button) answerGrid.getFellow("answer3_"+questionCount, true);

			if (currentCorrectIndex == 1) {
				if (new Random().nextInt(2) == 0) {
					setLabels(answer1, answer2, answer3, quest);
				} else {
					setLabels(answer1, answer3, answer2, quest);
				}
			} else if (currentCorrectIndex == 2) {
				if (new Random().nextInt(2) == 0) {
					setLabels(answer2, answer1, answer3, quest);
				} else {
					setLabels(answer2, answer3, answer1, quest);
				}
			} else {
				if (new Random().nextInt(2) == 0) {
					setLabels(answer3, answer2, answer1, quest);
				} else {
					setLabels(answer3, answer1, answer2, quest);
				}
			}
		}

		EventListener<Event> listener = event -> {
			String id = event.getTarget().getId();
			Integer questionIndex = Integer.parseInt(id.substring(id.lastIndexOf("_")+1));
			Integer answerIndex = Integer.parseInt(id.substring(id.lastIndexOf("_")-1, id.lastIndexOf("_")));
			int correctIndex = correctIndexes[questionIndex-1];

			if (correctIndex == answerIndex) {
				if (++correctAnswerCount == correctIndexes.length) {
					showNextWindow();
				} else {
					event.getTarget().getParent().getParent().getParent().setVisible(false);
				}
			} else {
				wrongAnswer();
			}
		};

		for (int i=1; i<=questionCount; i++) {
			for (int j=1; j<=3; j++) {
				Button btn = (Button) answerGrid.getFellow("answer"+j+"_"+i, true);
				btn.addEventListener(Events.ON_CLICK, listener);
			}
		}
	}

	private void setLabels(Button correct, Button b2, Button b3, Question question) {
		correct.setLabel(question.getCorrectAnswer().getText());
		Set<Answer> answers = question.getAnswers();
		boolean first = true;
		for (Answer ans : answers) {
			if (!ans.isCorrect()) {
				if (first) {
					b2.setLabel(ans.getText());
					first = false;
				} else {
					b3.setLabel(ans.getText());
				}
			}
		}
	}

	@Listen("onClick = #cancelButton")
	public void doCancel() {
		close(ExecuteResult.CANCEL);
	}

	private void showNextWindow() {
		readTaskWin.doOverlapped();
		readTaskWin.setVisible(false);

		Map<Object, Object> params = new HashMap<>();
		params.put("task", task);
		params.put("test", true);
		Window exec = (Window) Executions.createComponents("/work/mytasks/execute/3_surfing.zul", null, params);
		exec.setPosition("center,center");
		exec.setDraggable("false");
		if (!exec.getEventListeners(Events.ON_CLOSE).iterator().hasNext()) {
			exec.addEventListener(Events.ON_CLOSE, event -> {
				if (event.getData() != null) {
					Events.postEvent(new Event(Events.ON_CLOSE, readTaskWin, event.getData()));
				}
				SessionUtil.removeExecutingTask();
				readTaskWin.detach();
			});
		}
		exec.doHighlighted();
	}

	private void close(ExecuteResult res) {
		SessionUtil.removeExecutingTask();
		Events.postEvent(new Event(Events.ON_CLOSE, readTaskWin, res));
		readTaskWin.detach();
	}

	private void wrongAnswer() {
		task = taskService.getTask(task.getTaskId());
		UserCredential credential = authService.getUserCredential();
		Person p = credential.getPerson();
		PersonTask pt = taskService.findPersonTask(task, p);
		if (pt == null) {
			pt = new PersonTask();
			pt.setPk(new PersonTaskId(p, task));
		} else {
			if (pt.getStatus() == 1) {
				Clients.showNotification("Задание уже выполнено", "error", null, "after_start", 2000);
				return;
			}
		}

		pt.setAdded(new Date());
		pt.setIp(credential.getIp());
		pt.setConfirm("");
		pt.setStatus(3);
		task.getExecutors().add(pt);
		taskService.save(task);

		Clients.showNotification("Неверный ответ", "error", null, "middle_center", 2000);
		close(ExecuteResult.WRONG_ANSWER);
	}
}
