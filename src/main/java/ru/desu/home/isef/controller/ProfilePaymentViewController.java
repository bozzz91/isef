package ru.desu.home.isef.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PaymentService;
import ru.desu.home.isef.services.auth.AuthenticationService;

import java.util.List;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProfilePaymentViewController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;

    @Wire Grid payGrid;

	@WireVariable AuthenticationService authService;
    @WireVariable PaymentService paymentService;
 
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
		Person currentPerson = authService.getUserCredential().getPerson();
        List<Payment> allMyPayments = paymentService.findRepayments(currentPerson);
        ListModelList<Payment> model = new ListModelList<>(allMyPayments);
        payGrid.setModel(model);
    }
}
