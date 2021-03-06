package ru.desu.home.isef.controller.admin;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.controller.tasks.MyTaskListAbstractController;
import ru.desu.home.isef.entity.Status;
import ru.desu.home.isef.entity.Task;

import java.util.List;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AllTasksOnModerController extends MyTaskListAbstractController {
    private static final long serialVersionUID = 1L;

    //wire components
    @Wire Textbox curTaskRemark;

	@Override
	protected void initModel() {
		List<Task> todoList = taskService.getTasksByStatus(Status._2_MODER);
		taskListModel = new ListModelList<>(todoList);
		taskList.setModel(taskListModel);
	}

	@Listen("onClick = #publishTask")
    public void doPublishTask() {
        String msg = "Публикация задания";
        Messagebox.show(msg,
                "Подтверждение публикации",
                Messagebox.YES | Messagebox.CANCEL,
                Messagebox.QUESTION,
				event -> {
					if (event.getName().equals(Messagebox.ON_YES)) {
						int index = taskListModel.indexOf(curTask);
						curTask.setStatus(Status._3_PUBLISH);
						taskService.save(curTask);
						taskListModel.remove(index);
						curTask = null;
						refreshDetailView();
						Clients.showNotification("Задание сохранено и опубликовано", "info", taskList, "bottom_right", 5000);
					}
				});
    }

    @Listen("onClick = #cancelTask")
    public void doCancelTask() {
        if (Strings.isBlank(curTaskRemark.getValue())) {
            Clients.showNotification("Введите причину отказа", "warning", curTaskRemark, "after_end", 3000);
            return;
        }

        String msg = "Отказ задания";
        Messagebox.show(msg,
                "Отказ публикации",
                Messagebox.YES | Messagebox.CANCEL,
                Messagebox.QUESTION,
				event -> {
					if (event.getName().equals(Messagebox.ON_YES)) {
						int index = taskListModel.indexOf(curTask);
						curTask.setStatus(Status._1_DRAFT);
						curTask.setRemark(curTaskRemark.getValue());
						taskService.save(curTask);
						taskListModel.remove(index);
						curTask = null;
						refreshDetailView();
						Clients.showNotification("Задание отказано и возвращено автору", "info", taskList, "bottom_right", 5000);
					}
				});
    }
}
