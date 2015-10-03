package ru.desu.home.isef.controller.tasks.create;

import lombok.extern.java.Log;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.*;
import ru.desu.home.isef.services.BanService;
import ru.desu.home.isef.services.CountryService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.utils.ConfigUtil;
import ru.desu.home.isef.utils.FormatUtil;

import java.util.*;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public abstract class AbstractCreateTaskController extends SelectorComposer<Component> {
    
    //components
    protected @Wire Spinner countSpin;
    protected @Wire Label personCashLabel;
    protected @Wire Textbox resultCost;
    protected @Wire Window createTaskWin;

	protected @Wire("#taskPropertyGrid #curTaskDate")       	Label curTaskDate;
	protected @Wire("#taskPropertyGrid #labelTaskType")         Label labelTaskType;
    protected @Wire("#taskPropertyGrid #curTaskSubjectEdit")    Textbox curTaskSubjectEdit;
    protected @Wire("#taskPropertyGrid #curTaskDescription")    Textbox curTaskDescription;
	protected @Wire("#taskPropertyGrid #curTaskRemark")    		Textbox curTaskRemark;
	protected @Wire("#taskPropertyGrid #curTaskConfirm")    	Textbox curTaskConfirm;
	protected @Wire("#taskPropertyGrid #taskLink")          	Textbox taskLink;
	protected @Wire("#taskPropertyGrid #country")               Listbox country;
	protected @Wire("#taskPropertyGrid #questionRow")         	Row questionRow;
	protected @Wire("#taskPropertyGrid #addQuestion") 	      	Button addQuestion;
    
    //services
    protected @WireVariable AuthenticationService authService;
    protected @WireVariable PersonService personService;
    protected @WireVariable TaskService taskService;
	protected @WireVariable BanService banService;
	protected @WireVariable CountryService countryService;
	protected @WireVariable ConfigUtil config;
    
    protected Double cost = 0.0;
    protected TaskType curTaskType;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        Person p = authService.getUserCredential().getPerson();
        p = personService.find(p.getEmail());
        personCashLabel.setValue("Ваш баланс: " + p.getCash());

		List<Country> countries = countryService.findAll();
		ListModelList<Country> model = new ListModelList<>(countries);
		model.setMultiple(true);
		for (Country c : countries) {
			model.addToSelection(c);
		}
		country.setModel(model);
		country.setCheckmark(true);
		country.setMultiple(true);

		Map<?, ?> args = Executions.getCurrent().getArg();
        curTaskType = (TaskType) args.get("taskType");
        labelTaskType.setValue(curTaskType.getType());
        cost = calcCost();
        resultCost.setValue("Стоимость : " + cost);

		setVisible(addQuestion.getParent().getParent(), curTaskType.isTest());
		addQuestion.setLabel("Добавить вопрос за " + config.getAdditionalQuestionCost() + " iCoin");
		setVisible(questionRow, curTaskType.isTest() || curTaskType.isQuestion());
    }
    
    public abstract Task doCreateTask(Person p);
    
    protected void setVisible(Component comp, boolean visible) {
        if (comp != null) {
            comp.setVisible(visible);
        }
    }
    
    @Listen("onClick = #doCreateTask")
    public void validateAndCreateTask() {
		if (countSpin.getValue() != null && countSpin.getValue() <= 0) {
			Clients.showNotification("Задано неверное кол-во кликов/переходов", "error", countSpin, "after_end", 3000);
			return;
		}
		if (Strings.isBlank(curTaskSubjectEdit.getValue())) {
			Clients.showNotification("Введите название задания", "error", curTaskSubjectEdit, "after_end", 3000);
			return;
		}
		if (Strings.isBlank(taskLink.getValue())) {
			Clients.showNotification("Укажите ссылку вашего сайта для задания", "error", taskLink, "after_end", 3000);
			return;
		}
		if (Strings.isBlank(curTaskDescription.getValue())) {
			Clients.showNotification("Напишите описание/вопрос к вашему заданию", "error", curTaskDescription, "after_end", 3000);
			return;
		}
		if (curTaskDescription.getValue().length() >= 1000) {
			Clients.showNotification("Описание/Вопрос слишком длинные. Максимальное кол-во символов - 1000.", "error", curTaskDescription, "after_end", 3000);
			return;
		}

		Person p = authService.getUserCredential().getPerson();
		p = personService.findById(p.getId());
		if (p.getCash() < cost) {
			Clients.showNotification("Недостаточно средств на вашем балансе, чтобы создать данное задание", "warning", countSpin, "after_end", 3000);
			return;
		}

		boolean checked = checkIndividualTask();
		if (!checked) {
			return;
		}
        Task t = doCreateTask(p);
		if (t != null) {
			publishTask(t);
		}
    }
    
    @Listen("onChange = #countSpin; onCheck = #vip; onChange = #uniqueIp; onClick = #taskPropertyGrid #addQuestion")
    public void changeTotalCost() {
        cost = calcCost();
        resultCost.setValue("Стоимость : " + FormatUtil.formatDouble(cost));
    }

	protected abstract String getConfirmMessage();

	public void publishTask(final Task curTask) {
		String msg = "Публикация задания. Убедитесь, что все данные введены корректно" + getConfirmMessage();

		Map<String, String> params = new HashMap<>();
		params.put("width", "600");
		Messagebox.show(msg,
				"Подтверждение публикации",
				new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.CANCEL},
				new String[] {"Всё верно", "Отмена"},
				Messagebox.QUESTION,
				Messagebox.Button.OK,
				new EventListener<Messagebox.ClickEvent>() {
					@Override
					public void onEvent(Messagebox.ClickEvent event) throws Exception {
						if (event.getName().equals(Messagebox.ON_YES)) {
							String link = taskLink.getValue();
							if (!link.startsWith("http://") && !link.startsWith("https://")) {
								link = "http://" + link;
							}
							List<Ban> bans = banService.find(link);
							if (bans != null && !bans.isEmpty()) {
								Clients.showNotification("Сайт " + link + " занесен в черный список", "warning", null, "middle_center", 3000);
								return;
							}

							String taskNextStage;
							if (curTask.getTaskType().isQuestion() || curTask.getTaskType().isSurfing() || curTask.getTaskType().isTest()) {
								curTask.setStatus(Status._3_PUBLISH);
								taskNextStage = "опубликовано";
							} else {
								curTask.setStatus(Status._2_MODER);
								taskNextStage = "отправлено на модерацию";
							}

							curTask.setSubject(curTaskSubjectEdit.getValue());
							curTask.setLink(link);
							curTask.setDescription(curTaskDescription.getValue());
							curTask.setRemark(null);
							curTask.setCountries(getCountries());
							if (!Strings.isBlank(curTaskConfirm.getValue())) {
								curTask.setConfirmation(curTaskConfirm.getValue());
							}

							taskService.save(curTask);

							//show message for user
							Clients.showNotification("Задание сохранено и " + taskNextStage, "info", null, "middle_center", 3000);

							createTaskWin.detach();
						}
					}
				}, params);
	}

	protected Set<Country> getCountries() {
		Set<Country> countries = new HashSet<>();
		if (country.getSelectedCount() == country.getItemCount()) {
			return countries;
		}
		for (Listitem item : country.getSelectedItems()) {
			countries.add(item.<Country>getValue());
		}
		return countries;
	}

	protected abstract boolean checkIndividualTask();

	protected Double calcMultiplier() {
		return curTaskType.getMultiplier();
	}

	protected Double calcCost() {
		return calcMultiplier() * countSpin.getValue();
	}
}
