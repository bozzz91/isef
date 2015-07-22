package ru.desu.home.isef.controller.tasks.create;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;
import ru.desu.home.isef.utils.Config;
import ru.desu.home.isef.utils.SessionUtil;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CreateSurfingTaskController extends AbstractVariableCostTaskController {
    
    //wire components
    protected @Wire("#taskPropertyGrid #curTaskRemark")     Textbox curTaskRemark;
    protected @Wire("#taskPropertyGrid #curTaskConfirm")    Textbox curTaskConfirm;
    protected @Wire("#taskPropertyGrid #taskLink")          Textbox taskLink;
    protected @Wire("#taskPropertyGrid #curTaskDate")       Label curTaskDate;
    protected @Wire("#taskPropertyGrid #questionRow")       Row questionRow;

	protected @Wire Combobox showTo;

	//data for the view
	protected ListModelList<String> showToList;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        setVisible(curTaskRemark.getParent().getParent(), false);
		setVisible(questionRow, false);
		setVisible(curTaskDate.getParent().getParent(), false);
		setVisible(curTaskConfirm.getParent().getParent(), false);

		showToList = new ListModelList<>(Config.getAllReferals());
		showToList.addToSelection(Config.getFirstReferal());
		showTo.setModel(showToList);
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
			t.setShowTo(Config.getShowTo(showTo.getValue()));
			t.setUniqueIp(Config.getUniqueIp(uniqueIp.getValue()));
			t.setSex(Config.getSex(sex.getValue()));
        }
        
        curTask = taskService.saveTaskAndPerson(t, p);

        SessionUtil.removeCurTaskType();
        authService.getUserCredential().setPerson(p);
        personCashLabel.setValue("Ваш баланс: " + p.getCash());
        
        Events.postEvent(new Event(Events.ON_CLOSE , createTaskWin, curTask));
        
        createTaskWin.detach();
    }
}
