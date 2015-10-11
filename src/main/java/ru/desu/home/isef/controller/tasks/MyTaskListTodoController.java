package ru.desu.home.isef.controller.tasks;

import lombok.extern.java.Log;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.zul.Timer;
import ru.desu.home.isef.controller.tasks.execute.AbstractExecuteTaskController.ExecuteResult;
import ru.desu.home.isef.entity.*;
import ru.desu.home.isef.services.auth.UserCredential;
import ru.desu.home.isef.utils.SessionUtil;

import java.util.Calendar;
import java.util.*;
import java.util.logging.Level;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyTaskListTodoController extends MyTaskListAbstractController {
    private static final long serialVersionUID = 1L;

    //wire components
    @Wire Textbox taskSubject;
    @Wire Timer timer;
    @Wire Button cancelSearch, clBusy;
    @Wire Window busyWin;
    @Wire("#taskPropertyGrid #curTaskRemark") Textbox curTaskRemark;
    @Wire("#taskPropertyGrid #rowRemark") Row rowRemark;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
		curTaskAnswer.getParent().getParent().setVisible(false);
		curTaskAnswer1.getParent().getParent().setVisible(false);
		curTaskAnswer2.getParent().getParent().setVisible(false);
        clBusy = (Button) busyWin.getFellow("clBusy");
    }

	@Override
	protected void initModel() {
		//TODO перенести проверки на уровень SQL

		UserCredential credential = authService.getUserCredential();
		Person p = credential.getPerson();
		List<Task> todoList = taskService.getTasksForWork(p);
		List<Object[]> ptInfo = taskService.getTaskForWorkRemark(p);
		for (Object[] arr : ptInfo) {
			Long taskId = (Long)arr[0];
			String remark = (String)arr[1];
			todoList.stream().filter(t -> t.getTaskId().equals(taskId)).forEach(t -> t.setRemark(remark));
		}

		outer:
		for (ListIterator<Task> it = todoList.listIterator(); it.hasNext();) {
			Task t = it.next();

			//filter by user's sex
			if (t.getSex() != null && !t.getSex().equals("U")) {
				if(!p.getSex().equals(t.getSex())) {
					it.remove();
					continue;
				}
			}

			//filter by unique ip
			if (t.getUniqueIp() != null && t.getUniqueIp() > 0) {
				t = taskService.getTask(t.getTaskId());
				Set<PersonTask> pts = t.getExecutors();
				for (PersonTask pt : pts) {
					if (pt.getIp() != null) {
						if (t.getUniqueIp() == 1) {
							if (pt.getIp().equals(credential.getIp())) {
								it.remove();
								continue outer;
							}
						} else {
							String[] mask = pt.getIp().split("\\.");
							String[] ip = credential.getIp().split("\\.");
							if (mask[0].equals(ip[0]) && mask[1].equals(ip[1])) {
								it.remove();
								continue outer;
							}
						}
					}
				}
			}

			//filter by country
			if (!t.getCountries().isEmpty()) {
				boolean validCountry = false;
				for (Country country : t.getCountries()) {
					if (country.getCode().equals(credential.getCountryCode())) {
						validCountry = true;
						break;
					}
				}
				if (!validCountry) {
					it.remove();
					continue;
				}
			}

			//filter by person registration date
			if (t.getTaskType().isTest() && t.getRegistrationDayAgo() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(p.getCreationTime());
				cal.add(Calendar.DAY_OF_YEAR, t.getRegistrationDayAgo());
				if (new Date().before(cal.getTime())) {
					it.remove();
					continue;
				}
			}

			//filter by referal visibility
			if (t.getShowTo() != null && t.getShowTo() > 0) {
				if (t.getShowTo() == 1) {
					t = taskService.getTask(t.getTaskId());
					Person owner = personService.findById(t.getOwner().getId());
					Set<Person> refs = owner.getReferals();
					boolean found = false;
					for (Person ref : refs) {
						if (ref.getId().equals(p.getId())) {
							found = true;
							break;
						}
					}
					if (!found) {
						it.remove();
					}
				} else if (t.getShowTo() == 2) {
					p = personService.findById(p.getId());
					if (p.getInviter() != null) {
						it.remove();
					}
				}
			}
		}

		taskListModel = new ListModelList<>(todoList);
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

    @Listen("onClick = #execTask")
    public void doExecTask() {
		if (SessionUtil.isExecutingTask()) {
			Clients.showNotification("Вы уже выполняете другое задание", "warning", null, "middle_center", 3000, true);
			return;
		}
        String link = curTask.getLink();
		SessionUtil.setExecutingTask();
		if (curTask.getTaskType().isQuestion() || curTask.getTaskType().isSurfing() || curTask.getTaskType().isTest()) {
			Map<Object, Object> params = new HashMap<>();
			params.put("task", curTask);
			final int index = taskListModel.indexOf(curTask);
			Window exec = (Window) Executions.createComponents(curTask.getTaskType().getExecTemplate(), null, params);
			exec.setPosition("center,center");
			exec.setDraggable("false");
			if (!exec.getEventListeners(Events.ON_CLOSE).iterator().hasNext()) {
				exec.addEventListener(Events.ON_CLOSE, event -> {
					if (event.getData() != null) {
						if (event.getData() == ExecuteResult.SUCCESS) {
							taskListModel.remove(index);
							curTask = null;
							refreshDetailView();
							Clients.showNotification("Задание выполнено", "info", null, "middle_center", 1000, true);
						} else if (event.getData() == ExecuteResult.WRONG_ANSWER) {
							taskListModel.remove(index);
							curTask = null;
							refreshDetailView();
						}
					}
					SessionUtil.removeExecutingTask();
				});
			}
			exec.doHighlighted();
			return;
		}
        A a = (A) busyWin.getFellow("link");
        a.setHref(link);
        if (!clBusy.getEventListeners(Events.ON_CLICK).iterator().hasNext()) {
            clBusy.addEventListener(Events.ON_CLICK, event -> doClBusy());
        }
        busyWin.doHighlighted();
        timer.start();
        Clients.showBusy(clBusy, "Выполнение задания");
    }

    public void doClBusy() {
        busyWin.doOverlapped();
        busyWin.setVisible(false);
        try {
            if (!Strings.isBlank(curTask.getConfirmation())) {
                Window doConfirmWin = (Window) Executions.createComponents("/work/todolist/confirmWindow.zul", null, null);
                doConfirmWin.setPosition("center,center");
                doConfirmWin.setDraggable("false");
                doConfirmWin.addEventListener(Events.ON_CLOSE, event -> {
					if ((Boolean) event.getData()) {
						String conf = ((Textbox) event.getTarget().getFellow("confirm")).getValue();
						execTask(conf);
					}
					SessionUtil.removeExecutingTask();
				});
                ((Label) doConfirmWin.getFellow("todoLabel")).setValue(curTask.getDescription());
                ((Label) doConfirmWin.getFellow("confirmLabel")).setValue(curTask.getConfirmation());
                ((Label) doConfirmWin.getFellow("ipLabel")).setValue(SessionUtil.getIp());
                doConfirmWin.doHighlighted();
            } else {
                execTask("");
				SessionUtil.removeExecutingTask();
                Clients.showNotification("Готово", "info", null, "middle_center", 1000, true);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString());
            Clients.clearBusy(clBusy);
			SessionUtil.removeExecutingTask();
        }
    }

    @Listen("onTimer = #timer")
    public void saveExecResult() {
        timer.stop();
        Clients.clearBusy(busyWin.getFellow("clBusy"));
    }

    private void execTask(String confirm) {
        final int index = taskListModel.indexOf(curTask);
        curTask = taskService.getTask(curTask.getTaskId());
        Person p = authService.getUserCredential().getPerson();
        PersonTask pt = taskService.findPersonTask(curTask, p);
        boolean needInc = true;
        if (pt == null) {
            pt = new PersonTask();
            pt.setPk(new PersonTaskId(p, curTask));
            needInc = false;
        }
        pt.setAdded(new Date());
        pt.setIp(SessionUtil.getIp());
        pt.setConfirm(confirm);
        pt.setStatus(0);
        curTask.getExecutors().add(pt);

        if (needInc && curTask.incCountComplete() >= curTask.getCount()) {
            curTask.setStatus(Status._4_DONE);
        }
        taskService.save(curTask);
        taskListModel.remove(index);
        curTask = null;
        refreshDetailView();

		SessionUtil.removeExecutingTask();
    }
}
