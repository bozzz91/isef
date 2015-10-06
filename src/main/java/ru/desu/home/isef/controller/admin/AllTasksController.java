package ru.desu.home.isef.controller.admin;

import lombok.extern.java.Log;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import ru.desu.home.isef.controller.tasks.MyTaskListAbstractController;
import ru.desu.home.isef.entity.Task;

import java.util.List;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AllTasksController extends MyTaskListAbstractController {
    private static final long serialVersionUID = 1L;

    //wire components
    @Wire Textbox taskSubject;
    @Wire Button searchTask, cancelSearch, clBusy;
    @Wire("#taskPropertyGrid #curTaskRemark") Textbox curTaskRemark;
    @Wire("#taskPropertyGrid #rowRemark") Row rowRemark;

	@Override
	protected void initModel() {
		List<Task> allTasks = taskService.getTasks();
		taskListModel = new ListModelList<>(allTasks);
		taskList.setModel(taskListModel);
	}

	@Override
    @Listen("onSelect = #taskList")
    public void doTaskSelect() {
        if (taskListModel.isSelectionEmpty()) {
            curTask = null;
        } else {
            curTask = taskListModel.getSelection().iterator().next();
            rowRemark.setVisible(false);
        }
        refreshDetailView();
    }
    
    @Override
    protected void refreshDetailView() {
        super.refreshDetailView();

        if (curTask == null) {
            curTaskRemark.setValue(null);
            rowRemark.setVisible(false);
        } else {
            if (!Strings.isBlank(curTask.getRemark())) {
                curTaskRemark.setValue(curTask.getRemark());
                rowRemark.setVisible(true);
            }
        }
    }

    @Listen("onClick = #searchTask; onOK = #taskSubject")
    public void doSearchTask() {
        if (Strings.isBlank(taskSubject.getValue())) {
            Clients.showNotification("Задайте что ищем", "error", taskSubject, "after_start", 2000);
            return;
        }
        for (Listitem li : taskList.getItems()) {
            Task t = li.getValue();
			if (t == null) {
				li.setVisible(false);
				continue;
			}
			String search = taskSubject.getValue();
			String subj = t.getSubject();
			String link = t.getLink();
            if ((subj!= null && subj.contains(search)) || (link!=null && link.contains(search))) {
                li.setVisible(true);
            } else {
                li.setVisible(false);
            }
        }
        cancelSearch.setVisible(true);
        cancelSearch.getParent().invalidate();
    }

    @Listen("onClick = #cancelSearch")
    public void doCancelSearchTask() {
        for (Listitem li : taskList.getItems()) {
            li.setVisible(true);
        }
        cancelSearch.setVisible(false);
        cancelSearch.getParent().invalidate();
    }

	@Listen("onTaskDelete = #taskList")
	public void doTaskDelete(ForwardEvent evt) {
		Button btn = (Button) evt.getOrigin().getTarget();
		Listitem litem = (Listitem) btn.getParent().getParent();

		final Task task = litem.getValue();

		Messagebox.show("Уверенны что хотите удалить задание?" + task.getSubject() + "\"",
				"Подтверждение удаления",
				Messagebox.YES | Messagebox.CANCEL,
				Messagebox.QUESTION,
				new EventListener<Event>() {
					@Override
					public void onEvent(Event event) throws Exception {
						if (event.getName().equals(Messagebox.ON_YES)) {

							//delete task
							taskService.delete(task);

							/*
							double cost = task.getCost();
							Person p = authService.getUserCredential().getPerson();
							p = personService.findById(p.getId());
							p.setCash(p.getCash() + cost);
							personService.save(p);
							authService.getUserCredential().setPerson(p);
							*/

							//personCashLabel.setValue("Ваш баланс: " + p.getCash());
							//update the model of listbox
							taskListModel.remove(task);

							if (task.equals(curTask)) {
								//refresh selected task view
								curTask = null;
								refreshDetailView();
							}
						}
					}
				});
	}
}
