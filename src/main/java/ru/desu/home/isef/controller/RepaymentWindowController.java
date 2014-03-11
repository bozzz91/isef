package ru.desu.home.isef.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonWallet;
import ru.desu.home.isef.services.PaymentService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RepaymentWindowController extends SelectorComposer<Component> {

    private static final String ISEF_MINIMUM_REPAY;
    
    static {
        Properties props = new Properties();
        try {
            props.load(LoginController.class.getResourceAsStream("/config.txt"));
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка при чтении конфига config.txt", e);
        }
        ISEF_MINIMUM_REPAY = props.getProperty("minimum_pay");

        if (StringUtils.isEmpty(ISEF_MINIMUM_REPAY))
            throw new IllegalArgumentException("Неверный параметр 'minimum_pay' в config.txt");
        try {
            Integer.parseInt(ISEF_MINIMUM_REPAY);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный параметр 'minimum_pay' в config.txt", e);
        }
    }
    
    @Wire
    Intbox summ;
    @Wire
    Label summrub, total;
    @Wire
    Combobox wallet;
    @Wire
    Window doRePayWin;

    @WireVariable
    AuthenticationService authService;
    @WireVariable
    PersonService personService;
    @WireVariable
    PaymentService paymentService;

    double currency;
    Person currPerson;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        currency = paymentService.getCurrency().getCurrency();
        currPerson = personService.findById(authService.getUserCredential().getPerson().getId());
        List<PersonWallet> wallets = currPerson.getWallets();
        wallet.setModel(new ListModelList<>(wallets));
        total.setValue(currPerson.getCash()+" iCoin");
        summrub.setValue(0 + " руб.");
    }

    @Listen("onClick = #doPayButton")
    public void doPay() {
        if (summ.getValue() != null && summ.getValue() < 50) {
            Clients.showNotification("Указана неверная сумма", "error", summ, "after_end", 3000);
            return;
        }
        currPerson = personService.find(currPerson.getEmail());
        if (summ.getValue() > currPerson.getCash()) {
            Clients.showNotification("Указана сумма больше, чем у Вас имеется на балансе", "error", summ, "after_end", 3000);
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

        Map params = new HashMap();
        params.put("width", 400);
        Messagebox.show("Запрос на вывод средств успешно добавлен.",
                "Совершение выплаты",
                new Messagebox.Button[]{Messagebox.Button.OK},
                new String[]{"Ok"},
                Messagebox.INFORMATION,
                Messagebox.Button.OK,
                new SerializableEventListener<Messagebox.ClickEvent>() {

                    @Override
                    public void onEvent(Messagebox.ClickEvent event) throws Exception {
                        if(EventQueues.exists("getCash")) {
                            Event ev = new Event("onGetCash", null, currPerson.getCash()+" iCoin");
                            EventQueues.lookup("getCash").publish(ev);
                        }
                        doRePayWin.detach();
                    }
                },
                params);
    }

    @Listen("onChange = #summ")
    public void changeSumm() {
        currPerson = personService.findById(currPerson.getId());
        if (summ.getValue() > currPerson.getCash() || summ.getValue() < 50) {
            throw new WrongValueException(summ, "Неверная сумма");
        }
        summrub.setValue(summ.getValue() * currency + "");
    }
}
