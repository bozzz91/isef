package ru.desu.home.isef.controller;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ConfirmWindowController extends SelectorComposer<Component> {
    @Wire
    Window confirmWin;
    @Wire
    Textbox confirm;
    
    @Listen("onClick = #confirmTaskButton")
    public void doConfirm() {
        if (Strings.isBlank(confirm.getValue())) {
            return;
        }
        Events.postEvent(new Event(Events.ON_CLOSE ,confirmWin, true)) ;
        confirmWin.detach();
    }
    
    @Listen("onClick = #cancelTaskButton")
    public void doCancel() {
        Events.postEvent(new Event(Events.ON_CLOSE ,confirmWin, false)) ;
        confirmWin.detach();
    }
}
