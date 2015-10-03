package ru.desu.home.isef.controller.tasks.create;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CreateSurfingTaskController extends AbstractVariableCostTaskController {
    
    //wire components
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

		showToList = new ListModelList<>(config.getAllReferals());
		showToList.addToSelection(config.getFirstReferal());
		showTo.setModel(showToList);
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
        t.setConfirmation(curTaskConfirm.getValue());
        t.setOwner(authService.getUserCredential().getPerson());
        
        if (curTaskType.isSurfing()) {
            t.setVip(vip.isChecked());
			t.setShowTo(config.getShowTo(showTo.getValue()));
			t.setUniqueIp(config.getUniqueIp(uniqueIp.getValue()));
			t.setSex(config.getSex(sex.getValue()));
        }

		p.setCash(p.getCash() - t.getCost());
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
		return true;
	}
}
