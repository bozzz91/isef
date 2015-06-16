package ru.desu.home.isef.controller.admin;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import ru.desu.home.isef.services.TaskService;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class UtilsViewController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @WireVariable
    TaskService taskService;
    @WireVariable
    AuthenticationService authService;
    
    @Listen("onClick = #refreshAllTasks")
    public void refreshAll() {
        Clients.showBusy("Обновление заданий");
        int cnt = taskService.refreshAllTasks();
        Clients.clearBusy();
        Clients.alert("Сброшено записей: " + cnt);
    }
    
    @Listen("onClick = #refreshMyTasks")
    public void refreshMy() {
        Clients.showBusy("Обновление заданий");
        int cnt = taskService.refreshMyTasks(authService.getUserCredential().getPerson().getId());
        Clients.clearBusy();
        Clients.alert("Сброшено записей: " + cnt, "Info", "INFORMATION");
    }
}
