package ru.desu.home.isef.controller.tasks.execute;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.services.CaptchaService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.auth.AuthenticationService;

import java.util.Map;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AbstractExecuteTaskController extends SelectorComposer<Component> {

	public enum ExecuteResult {
		SUCCESS,
		CANCEL,
		WRONG_ANSWER
	}

	protected Task task;

	protected @WireVariable CaptchaService captchaService;
	protected @WireVariable TaskService taskService;
	protected @WireVariable AuthenticationService authService;
	protected @WireVariable PersonService personService;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Map<?, ?> args = Executions.getCurrent().getArg();
		task = (Task) args.get("task");
	}
}
