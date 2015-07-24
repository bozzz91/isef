package ru.desu.home.isef.controller.tasks.execute;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.*;
import ru.desu.home.isef.entity.Captcha;
import ru.desu.home.isef.utils.Config;

import java.util.Date;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ExecuteSurfingController extends AbstractExecuteTaskController {

	@Wire Iframe frame;
	@Wire Timer timer;

	@Wire Row rowTimer;
	@Wire Row rowCaptcha;
	@Wire Image image;
	@Wire Textbox confirm;
	@Wire Label countdown;
	@Wire Window execTaskWin;

	private Captcha cap;
	private int delay = 60;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		frame.setSrc(task.getLink());
		delay = task.getWatchTime().intValue();
	}

	@Listen("onTimer = #timer")
	public void showCaptcha() {
		if (delay-- == 0) {
			timer.stop();

			cap = captchaService.findRandom();
			image.setSrc("/imgs/captcha/" + cap.getImage());
			rowTimer.setVisible(false);
			rowCaptcha.setVisible(true);
		} else {
			countdown.setValue(delay + " сек...");
		}
	}

	@Listen("onClick = #confirmCaptchaButton; onOK = #confirm")
	public void checkCaptcha() {
		if (cap.getAnswer().equalsIgnoreCase(confirm.getValue())) {
			execTask();
		} else {
			cap = captchaService.findRandom();
			image.setSrc("/imgs/captcha/" + cap.getImage());
			Clients.showNotification("Неверный ответ", "error", confirm, "before_start", 2000);
		}
	}

	@Listen("onClick = #cancelButton")
	public void cancelCaptcha() {
		Events.postEvent(new Event(Events.ON_CLOSE, execTaskWin, false));
		execTaskWin.detach();
	}

	private void execTask() {
		task = taskService.getTask(task.getTaskId());
		Person p = authService.getUserCredential().getPerson();
		PersonTask pt = taskService.findPersonTask(task, p);
		boolean needInc = true;
		if (pt == null) {
			pt = new PersonTask();
			pt.setPk(new PersonTaskId(p, task));
			needInc = false;
		} else {
			if (pt.getStatus() == 1) {
				Clients.showNotification("Задание уже выполнено", "error", null, "after_start", 2000);
				return;
			}
		}
		pt.setAdded(new Date());
		pt.setIp(Config.getIp());
		pt.setConfirm("");
		pt.setStatus(0);
		task.getExecutors().add(pt);
		if (needInc && task.incCountComplete() >= task.getCount()) {
			task.setStatus(Status._4_DONE);
		}
		taskService.save(task);
		taskService.donePersonTask(pt);

		Events.postEvent(new Event(Events.ON_CLOSE, execTaskWin, true));
		execTaskWin.detach();
	}

	@Listen("onFrameLoaded = #frame")
	public void frameLoaded() {
		timer.start();
	}
}
