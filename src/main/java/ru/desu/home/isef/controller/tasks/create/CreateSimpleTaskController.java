package ru.desu.home.isef.controller.tasks.create;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Row;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Task;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CreateSimpleTaskController extends AbstractCreateTaskController {
    
    //wire components
    protected @Wire("#taskPropertyGrid #questionRow")       Row questionRow;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        setVisible(questionRow, false);
        setVisible(curTaskRemark.getParent().getParent(), false);
		setVisible(curTaskDate.getParent().getParent(), false);
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

        p.setCash(p.getCash() - t.getCost());

        t = taskService.saveTaskAndPerson(t, p);
        authService.getUserCredential().setPerson(p);
        personCashLabel.setValue("Ваш баланс: " + p.getCash());

		return t;
    }

	@Override
	protected boolean checkIndividualTask() {
		return true;
	}

	@Override
	protected String getConfirmMessage() {
		return " для успешного прохождения этапа модерации." +
				"\n\nЕсли у модератора возникнут претензии к введенным значениям," +
				" то он может вернуть его Вам на корректирование с указанием своих примечаний";
	}
}
