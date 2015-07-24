package ru.desu.home.isef.controller.tasks.execute;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import ru.desu.home.isef.entity.Answer;
import ru.desu.home.isef.utils.SessionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ExecuteQuestionController extends AbstractExecuteTaskController {

	@Wire Textbox text;
	@Wire Window readTaskWin;
	@Wire Label question;
	@Wire Button answer1;
	@Wire Button answer2;
	@Wire Button answer3;

	private int correctIndex = -1;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		text.setValue(task.getDescription());
		question.setValue(task.getQuestion().getText());
		correctIndex = new Random().nextInt(3)+1;
		if (correctIndex == 1) {
			if (new Random().nextInt(2) == 0) {
				setLabels(answer1, answer2, answer3);
			} else {
				setLabels(answer1, answer3, answer2);
			}
		} else if (correctIndex == 2) {
			if (new Random().nextInt(2) == 0) {
				setLabels(answer2, answer1, answer3);
			} else {
				setLabels(answer2, answer3, answer1);
			}
		} else {
			if (new Random().nextInt(2) == 0) {
				setLabels(answer3, answer2, answer1);
			} else {
				setLabels(answer3, answer1, answer2);
			}
		}
	}

	private void setLabels(Button correct, Button b2, Button b3) {
		correct.setLabel(task.getQuestion().getCorrectAnswer().getText());
		Set<Answer> answers = task.getQuestion().getAnswers();
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

	@Listen("onClick = #answer1")
	public void doAnswer1() {
		if (correctIndex == 1){
			showNextWindow();
		} else {
			close();
		}
	}

	@Listen("onClick = #answer2")
	public void doAnswer2() {
		if (correctIndex == 2){
			showNextWindow();
		} else {
			close();
		}
	}

	@Listen("onClick = #answer3")
	public void doAnswer3() {
		if (correctIndex == 3){
			showNextWindow();
		} else {
			close();
		}
	}

	@Listen("onClick = #cancelButton")
	public void doCancel() {
		close();
	}

	private void showNextWindow() {
		readTaskWin.doOverlapped();
		readTaskWin.setVisible(false);

		Map<Object, Object> params = new HashMap<>();
		params.put("task", task);
		Window exec = (Window) Executions.createComponents("/work/mytasks/execute/3_surfing.zul", null, params);
		exec.setPosition("center,center");
		exec.setDraggable("false");
		if (!exec.getEventListeners(Events.ON_CLOSE).iterator().hasNext()) {
			exec.addEventListener(Events.ON_CLOSE, new SerializableEventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					if (event.getData() != null) {
						Events.postEvent(new Event(Events.ON_CLOSE, readTaskWin, event.getData()));
					}
					SessionUtil.removeExecutingTask();
					readTaskWin.detach();
				}
			});
		}
		exec.doHighlighted();
	}

	private void close() {
		SessionUtil.removeExecutingTask();
		Clients.showNotification("Неверный ответ", "error", null, "middle_center", 2000);
		Events.postEvent(new Event(Events.ON_CLOSE, readTaskWin, false));
		readTaskWin.detach();
	}
}
