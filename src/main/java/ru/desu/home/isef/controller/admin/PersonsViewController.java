package ru.desu.home.isef.controller.admin;

import java.util.ArrayList;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonWallet;
import ru.desu.home.isef.services.PaymentService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PersonsViewController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    Grid payGrid;

    @WireVariable
    AuthenticationService authService;
    @WireVariable
    PersonService personService;
    @WireVariable
    PaymentService paymentService;

    List<PersonWallet> pwToDelete = new ArrayList<>();
 
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        List<Person> allPersons = personService.findAll();
        ListModelList<Person> model = new ListModelList<>(allPersons);
        payGrid.setModel(model);
    }
    /*
    @Listen("onPaymentDelete = #payGrid")
    public void deletePayment(ForwardEvent evt) {
        Button btn = (Button) evt.getOrigin().getTarget();
        Row row = (Row) btn.getParent();
        Payment pw = row.getValue();
        
        pw.setStatus(3);
        pw.getPayOwner().addCash(pw.getOrderAmount());
        personService.save(pw.getPayOwner());
        paymentService.save(pw);
        
        ((ListModelList<Payment>) payGrid.<Payment>getModel()).remove(pw);
    }

    @Listen("onPaymentAccept = #payGrid")
    public void acceptPayment(ForwardEvent evt) {
        Button btn = (Button) evt.getOrigin().getTarget();
        Row row = (Row) btn.getParent();
        Payment pw = row.getValue();
        
        pw.setStatus(1);
        pw.setPayDate(new Date());
        pw.setBalanceAmount(pw.getOrderAmount());
        pw.setBalanceAmountRub(pw.getOrderAmountRub());
        paymentService.save(pw);
        
        ((ListModelList<Payment>) payGrid.<Payment>getModel()).remove(pw);
    }*/
}
