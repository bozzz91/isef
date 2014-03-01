package ru.desu.home.isef.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Window;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PaymentWindowController extends SelectorComposer<Component> {
    @Wire
    Intbox summ;
    @Wire
    Window doPayWin;
    
    @WireVariable
    AuthenticationService authService;
    @WireVariable
    PersonService personService;
    
    @Listen("onClick = #doPayButton")
    public void doPay() {
        if (summ.getValue() != null && summ.getValue() <= 0) {
            Clients.showNotification("Указана неверная сумма", "error", summ, "after_end", 3000);
            return;
        }
        Person p = authService.getUserCredential().getPerson();
        p = personService.find(p.getEmail());
        p.addCash(summ.getValue());
        personService.save(p);
        authService.getUserCredential().setPerson(p);

        EventQueues.lookup("cash", true).publish(new Event("onChange", null, p.getCash()));
        summ.setValue(1);
        doPayWin.onClose();
    }
}
