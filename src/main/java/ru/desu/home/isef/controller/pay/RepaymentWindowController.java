package ru.desu.home.isef.controller.pay;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonWallet;
import ru.desu.home.isef.services.PaymentService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.utils.ConfigUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RepaymentWindowController extends SelectorComposer<Component> {

    @Wire Intbox summ;
    @Wire Label summrub, total;
    @Wire Combobox wallet;
    @Wire Window doRePayWin;

    @WireVariable AuthenticationService authService;
    @WireVariable PersonService personService;
    @WireVariable PaymentService paymentService;
	@WireVariable ConfigUtil config;

    double currency;
    Person currPerson;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        currency = paymentService.getCurrency();
        currPerson = personService.findById(authService.getUserCredential().getPerson().getId());
        List<PersonWallet> wallets = currPerson.getWallets();
        wallet.setModel(new ListModelList<>(wallets));
        total.setValue(currPerson.getCash()+" iCoin" + (currPerson.isWebmaster() ? " ("+currPerson.getReserv() +" резерв)" : ""));
        summrub.setValue(0 + " руб.");
    }

    @Listen("onClick = #doPayButton")
    public void doPay() {
        boolean master = authService.getUserCredential().getPerson().isWebmaster();
        if (summ.getValue() == null) {
            Clients.showNotification("Указана неверная сумма", "error", summ, "after_end", 3000);
            return;
        }
		Integer minimumRepay = config.getMinimumRepay();
        if (summ.getValue() < minimumRepay) {
            Clients.showNotification("Указана неверная сумма, минимум - " + minimumRepay + " iCoin", "error", summ, "after_end", 3000);
            return;
        }
        currPerson = personService.find(currPerson.getEmail());
        double minCash = master ? currPerson.getCash()-currPerson.getReserv() : currPerson.getCash();
        if (summ.getValue() > minCash) {
            Clients.showNotification("Неверная сумма. Доступно для вывода: "+minCash, "error", summ, "after_end", 3000);
            return;
        }
        if (wallet.getSelectedIndex() == -1) {
            Clients.showNotification("Не выбран кошелек", "error", wallet, "after_end", 3000);
            return;
        }
        PersonWallet selectedWallet = wallet.getSelectedItem().getValue();

        Payment pay = new Payment();
        pay.setOrderAmount(summ.getValue() * 1.0);
        pay.setOrderAmountRub(summ.getValue() * currency);
        pay.setOrderDate(new Date());
        pay.setStatus(0);
        pay.setType(1);
        pay.setWallet(selectedWallet);
        pay.setPayOwner(currPerson);

        paymentService.save(pay);
        currPerson.addCash(-summ.getValue());
        personService.save(currPerson);
        authService.getUserCredential().setPerson(currPerson);

        Map<String, String> params = new HashMap<>();
        params.put("width", "400");
        Messagebox.show("Запрос на вывод средств успешно добавлен. Выплата будет произведена в течение суток.",
                "Совершение выплаты",
                new Messagebox.Button[]{Messagebox.Button.OK},
                new String[]{"Ok"},
                Messagebox.INFORMATION,
                Messagebox.Button.OK,
				event -> {
					if (EventQueues.exists("getCash")) {
						Event ev = new Event("onGetCash", null, currPerson.getCash()+" iCoin");
						EventQueues.lookup("getCash").publish(ev);
					}
					doRePayWin.detach();
				},
                params);
    }

    @Listen("onChange = #summ")
    public void changeSumm() {
        currPerson = personService.findById(currPerson.getId());
        double minCash = currPerson.isWebmaster() ? currPerson.getCash()-currPerson.getReserv() : currPerson.getCash();
        if (summ.getValue() > minCash || summ.getValue() < config.getMinimumRepay()) {
            throw new WrongValueException(summ, "Неверная сумма");
        }
        summrub.setValue(summ.getValue() * currency + "");
    }
}
