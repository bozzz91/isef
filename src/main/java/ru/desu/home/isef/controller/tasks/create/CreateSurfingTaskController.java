package ru.desu.home.isef.controller.tasks.create;

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
import ru.desu.home.isef.utils.FormatUtil;
import ru.desu.home.isef.utils.SessionUtil;

import java.util.Arrays;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CreateSurfingTaskController extends AbstractCreateTaskController {
    
    //wire components
    protected @Wire("#taskPropertyGrid #curTaskRemark")     Textbox curTaskRemark;
    protected @Wire("#taskPropertyGrid #curTaskConfirm")    Textbox curTaskConfirm;
    protected @Wire("#taskPropertyGrid #taskLink")          Textbox taskLink;
    protected @Wire("#taskPropertyGrid #curTaskDate")       Label curTaskDate;
    protected @Wire("#taskPropertyGrid #questionRow")       Row questionRow;
	protected @Wire Checkbox vip;
	protected @Wire Combobox uniqueIp;
	protected @Wire Combobox showTo;
	protected @Wire Combobox sex;

	//data for the view
	protected ListModelList<String> ipList;
	protected ListModelList<String> showToList;
	protected ListModelList<String> sexList;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        setVisible(curTaskRemark.getParent().getParent(), false);
		setVisible(questionRow, false);
		setVisible(curTaskDate.getParent().getParent(), false);
		setVisible(curTaskConfirm.getParent().getParent(), false);

		ipList = new ListModelList<>(Arrays.asList("Нет", "Да", "По маске 255.255."));
		ipList.addToSelection("Нет");
		uniqueIp.setModel(ipList);

		showToList = new ListModelList<>(Arrays.asList("Всем", "Моим рефералам", "Без рефералов"));
		showToList.addToSelection("Всем");
		showTo.setModel(showToList);

		sexList = new ListModelList<>(Arrays.asList("Всем", "M", "Ж"));
		sexList.addToSelection("Всем");
		sex.setModel(sexList);
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
        
        if (curTaskType.isSurfing()) {
            t.setVip(vip.isChecked());
			t.setUniqueIp(uniqueIp.getValue());
			t.setShowTo(showTo.getValue());
			t.setSex(sex.getValue());
        }
        
        Task curTask = taskService.saveTaskAndPerson(t, p);
        SessionUtil.setCurTask(curTask);
        SessionUtil.removeCurTaskType();
        authService.getUserCredential().setPerson(p);
        personCashLabel.setValue("Ваш баланс: " + p.getCash());
        
        Events.postEvent(new Event(Events.ON_CLOSE , createTaskWin, t));
        
        createTaskWin.detach();
    }

	@Listen("onCheck = #vip")
	public void onVipChanged() {
		double multiplier = curTaskType.getMultiplier();
		if (uniqueIp.getSelectedIndex() > 0) {
			multiplier += 0.02;
		}
		if (vip.isChecked()) {
			multiplier += 0.01;
		}
		cost = calcCost(multiplier, countSpin.getValue());
		resultCost.setValue("Стоимость : " + FormatUtil.formatDouble(cost) + " iCoin");
	}

	@Listen("onChange = #uniqueIp")
	public void onUniqueIpChanged() {
		double multiplier = curTaskType.getMultiplier();
		if (uniqueIp.getSelectedIndex() > 0) {
			multiplier += 0.02;
		}
		if (vip.isChecked()) {
			multiplier += 0.01;
		}
		cost = calcCost(multiplier, countSpin.getValue());
		resultCost.setValue("Стоимость : " + FormatUtil.formatDouble(cost) + " iCoin");
	}
}
