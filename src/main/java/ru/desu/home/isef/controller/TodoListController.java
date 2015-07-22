package ru.desu.home.isef.controller;

import lombok.extern.java.Log;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.*;
import ru.desu.home.isef.utils.Config;
import ru.desu.home.isef.utils.SessionUtil;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TodoListController extends MyTaskListAbstractController {
    private static final long serialVersionUID = 1L;

    //wire components
    @Wire Textbox taskSubject;
    @Wire Timer timer;
    @Wire Button searchTask, cancelSearch, clBusy;
    @Wire Window busyWin;
    @Wire("#taskPropertyGrid #curTaskRemark") Textbox curTaskRemark;
    @Wire("#taskPropertyGrid #rowRemark") Row rowRemark;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Person p = authService.getUserCredential().getPerson();
        List<Task> todoList = taskService.getTasksForWork(p);
        List<Object[]> ptInfo = taskService.getTaskForWorkRemark(p);
        for (Object[] arr : ptInfo) {
            Long taskId = (Long)arr[0];
            String remr = (String)arr[1];
            for (Task t : todoList) {
                if (t.getTaskId().equals(taskId)) {
                    t.setRemark(remr);
                }
            }
        }

		for (ListIterator<Task> it = todoList.listIterator(); it.hasNext();) {
			Task t = it.next();
			boolean removed = false;
			if (t.getSex() != null && !t.getSex().equals("U")) {
			 	if(!p.getSex().equals(t.getSex())) {
					it.remove();
					removed = true;
				}
			}
			if (removed) {
				continue;
			}
			if (t.getUniqueIp() != null && t.getUniqueIp() > 0) {
				t = taskService.getTask(t.getTaskId());
				Set<PersonTask> pts = t.getExecutors();
				for (PersonTask pt : pts) {
					if (pt.getIp() != null) {
						if (t.getUniqueIp() == 1) {
							if (pt.getIp().equals(Config.getIp())) {
								it.remove();
								removed = true;
								break;
							}
						} else {
							String[] mask = pt.getIp().split("\\.");
							String[] ip = Config.getIp().split("\\.");
							if (mask[0].equals(ip[0]) && mask[1].equals(ip[1])) {
								it.remove();
								removed = true;
								break;
							}
						}
					}
				}
			}
			if (removed) {
				continue;
			}
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
				}
				if (t.getShowTo() == 2) {
					p = personService.findById(p.getId());
					if (p.getInviter() != null) {
						it.remove();
					}
				}
			}
		}
        
        taskListModel = new ListModelList<>(todoList);
        taskList.setModel(taskListModel);
        
        clBusy = (Button) busyWin.getFellow("clBusy");
    }
    
    @Override
    @Listen("onSelect = #taskList")
    public void doTaskSelect() {
        Task curTask;
        if (taskListModel.isSelectionEmpty()) {
            curTask = null;
        } else {
            curTask = taskListModel.getSelection().iterator().next();
            rowRemark.setVisible(false);
        }
        SessionUtil.setCurTask(curTask);
        refreshDetailView();
    }
    
    @Override
    protected void refreshDetailView() {
        super.refreshDetailView();
        
        Task curTask = SessionUtil.getCurTask();
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
        Task curTask = SessionUtil.getCurTask();
        String link = curTask.getLink();
        A a = (A) busyWin.getFellow("link");
        a.setHref(link);
        if (!clBusy.getEventListeners(Events.ON_CLICK).iterator().hasNext()) {
            clBusy.addEventListener(Events.ON_CLICK, new SerializableEventListener<Event>() {

                @Override
                public void onEvent(Event event) throws Exception {
                    doClBusy();
                }
            });
        }
        busyWin.doHighlighted();
        timer.start();
        Clients.showBusy(clBusy, "Выполнение задания");
    }

    public void doClBusy() {
        busyWin.doOverlapped();
        busyWin.setVisible(false);
        try {
            Task curTask = SessionUtil.getCurTask();
            if (!Strings.isBlank(curTask.getConfirmation())) {
                Window doConfirmWin = (Window) Executions.createComponents("/work/todolist/confirmWindow.zul", null, null);
                doConfirmWin.setPosition("center,center");
                doConfirmWin.setDraggable("false");
                doConfirmWin.addEventListener(Events.ON_CLOSE, new SerializableEventListener<Event>() {

                    @Override
                    public void onEvent(Event event) throws Exception {
                        if ((Boolean) event.getData() == true) {
                            String conf = ((Textbox) event.getTarget().getFellow("confirm")).getValue();
                            execTask(conf);
                        }
                    }
                });
                ((Label) doConfirmWin.getFellow("todoLabel")).setValue(curTask.getDescription());
                ((Label) doConfirmWin.getFellow("confirmLabel")).setValue(curTask.getConfirmation());
                ((Label) doConfirmWin.getFellow("ipLabel")).setValue(Config.getIp());
                doConfirmWin.doHighlighted();
            } else {
                execTask("");
                Clients.showNotification("Готово", "info", null, "middle_center", 1000, true);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString());
            Clients.clearBusy(clBusy);
        }
    }

    @Listen("onTimer = #timer")
    public void saveExecResult() {
        timer.stop();
        Clients.clearBusy(busyWin.getFellow("clBusy"));
    }

    private void execTask(String confirm) {
        Task curTask = SessionUtil.getCurTask();
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
        pt.setIp(Config.getIp());
        pt.setConfirm(confirm);
        pt.setStatus(0);
        curTask.getExecutors().add(pt);
        if (needInc && curTask.incCountComplete() >= curTask.getCount()) {
            curTask.setStatus(Status._4_DONE);
        }
        taskService.save(curTask);
        taskListModel.remove(index);
        SessionUtil.removeCurTask();
        refreshDetailView();
    }
}
