package ru.desu.home.isef.controller.admin;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import ru.desu.home.isef.services.TaskService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class UtilsViewController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    Button refreshTasks;
    
    @WireVariable
    TaskService taskService;
    
    @Listen("onClick = #refreshTasks")
    public void refresh() {
        Clients.showBusy("Обновление заданий");
        int cnt = taskService.refreshTasks();
        Clients.clearBusy();
        Clients.alert("Обновлено " + cnt + " записей");
    }
}
