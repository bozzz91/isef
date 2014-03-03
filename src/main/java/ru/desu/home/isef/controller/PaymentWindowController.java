package ru.desu.home.isef.controller;

import java.util.Date;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PaymentService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PaymentWindowController extends SelectorComposer<Component> {
    @Wire
    Intbox summ;
    @Wire
    Label summrub;
    @Wire
    Window doPayWin;
    
    @WireVariable
    AuthenticationService authService;
    @WireVariable
    PersonService personService;
    @WireVariable
    PaymentService paymentService;

    double currency;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        currency = paymentService.getCurrency().getCurrency();
    }
    
    @Listen("onClick = #doPayButton")
    public void doPay() {
        if (summ.getValue() != null && summ.getValue() <= 0) {
            Clients.showNotification("Указана неверная сумма", "error", summ, "after_end", 3000);
            return;
        }
        Person p = authService.getUserCredential().getPerson();
        p = personService.find(p.getEmail());
        
        Payment pay = new Payment();
        pay.setOrderAmount(summ.getValue()*1.0);
        pay.setOrderAmountRub(summ.getValue()*currency);
        pay.setOrderDate(new Date());
        pay.setStatus(0);
        pay.setPayOwner(p);
        
        paymentService.save(pay);
        
        StringBuilder link = new StringBuilder("https://secure.onpay.ru/pay/");
        link.append("isef_me/?f=7&pay_mode=fix&price=").append(Double.valueOf(summ.getValue().toString()));
        link.append("&currency=TST&pay_for=").append(pay.getId());
        link.append("&convert=yes&price_final=true&user_email=").append(p.getEmail());

        doPayWin.detach();
        Executions.sendRedirect(link.toString());
    }
    
    @Listen("onChange = #summ")
    public void changeSumm() {
        summrub.setValue(summ.getValue()*currency+"");
    }
}
