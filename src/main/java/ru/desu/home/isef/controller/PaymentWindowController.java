package ru.desu.home.isef.controller;

import lombok.extern.java.Log;
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
import ru.desu.home.isef.utils.ConfigUtil;
import ru.desu.home.isef.utils.FormatUtil;

import java.util.Date;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PaymentWindowController extends SelectorComposer<Component> {
    
    @Wire Intbox summ;
    @Wire Label summrub;
    @Wire Window doPayWin;
    
    @WireVariable AuthenticationService authService;
    @WireVariable PersonService personService;
    @WireVariable PaymentService paymentService;

    double currency;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        currency = paymentService.getCurrency();
    }
    
    @Listen("onClick = #doPayButton")
    public void doPay() {
        if (summ.getValue() != null && summ.getValue() <= 0) {
            Clients.showNotification("Указана неверная сумма", "error", summ, "after_end", 3000);
            return;
        }
        Person p = authService.getUserCredential().getPerson();
        p = personService.find(p.getEmail());
        
        String amount = FormatUtil.formatDouble(summ.getValue()*1.0);
        String amountRub = FormatUtil.formatDouble(summ.getValue()*currency, 2);
        Payment pay = new Payment();
        pay.setOrderAmount(Double.parseDouble(amount));
        pay.setOrderAmountRub(Double.parseDouble(amountRub));
        pay.setOrderDate(new Date());
        pay.setStatus(0);
        pay.setType(0);
        pay.setPayOwner(p);
        
        paymentService.save(pay);
        
        StringBuilder link = new StringBuilder("https://secure.onpay.ru/pay/");
        link.append("isef_me/?f=7&pay_mode=fix")
            .append("&price=")      .append(amountRub)
            .append("&currency=")   .append(ConfigUtil.ONPAY_CURRENCY)
            .append("&pay_for=")    .append(pay.getId())
            .append("&convert=")    .append(ConfigUtil.ONPAY_CONVERT)
            .append("&price_final=").append(ConfigUtil.ONPAY_PRICE_FINAL)
            .append("&user_email=") .append(p.getEmail());

        doPayWin.detach();
        Executions.sendRedirect("/pay.php?amount="+amountRub+"&email="+p.getEmail());
    }
    
    @Listen("onChange = #summ")
    public void changeSumm() {
		String summary = FormatUtil.formatDouble(summ.getValue() * currency, 2);
		String rub = summary.split("\\.")[0];
		String cop = summary.split("\\.")[1];
        summrub.setValue(rub+" руб. "+cop+" коп.");
    }
}
