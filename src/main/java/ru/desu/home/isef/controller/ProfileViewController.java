package ru.desu.home.isef.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import ru.desu.home.isef.entity.Payment;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Reverse;
import ru.desu.home.isef.entity.PersonWallet;
import ru.desu.home.isef.entity.PersonWalletId;
import ru.desu.home.isef.entity.Wallet;
import ru.desu.home.isef.repo.ReverseRepo;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.WalletService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.services.auth.UserCredential;
import ru.desu.home.isef.utils.DecodeUtil;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProfileViewController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;
    private static final String ISEF_MINIMUM_REPAY;
    private static final String ISEF_MINIMUM_REPAY_DAYS;
    
    static {
        Properties props = new Properties();
        try {
            props.load(LoginController.class.getResourceAsStream("/config.txt"));
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка при чтении конфига config.txt", e);
        }
        ISEF_MINIMUM_REPAY = props.getProperty("minimum_pay");
        ISEF_MINIMUM_REPAY_DAYS = props.getProperty("minimum_pay_day");

        if (StringUtils.isEmpty(ISEF_MINIMUM_REPAY))
            throw new IllegalArgumentException("Неверный параметр 'minimum_pay' в config.txt");
        if (StringUtils.isEmpty(ISEF_MINIMUM_REPAY_DAYS))
            throw new IllegalArgumentException("Неверный параметр 'minimum_pay_day' в config.txt");
        try {
            Integer.parseInt(ISEF_MINIMUM_REPAY);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный параметр 'minimum_pay' в config.txt", e);
        }
        try {
            Integer.parseInt(ISEF_MINIMUM_REPAY_DAYS);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный параметр 'minimum_pay_day' в config.txt", e);
        }
    }
    
    //wire components
    @Wire
    Label account, cash, inviter, inviters, popupLabel;
    @Wire
    Textbox passBox, passRepeatBox, nickname, fullName, phone, walletName, refCode;
    @Wire
    Datebox birthday;
    @Wire
    Button changePass, getCash, copy;
    @Wire
    Row pass1, pass2;
    @Wire
    Combobox walletType, reverse;
    @Wire
    Grid profileGrid;

    @WireVariable
    AuthenticationService authService;
    @WireVariable
    PersonService personService;
    @WireVariable
    WalletService walletService;
    @WireVariable
    ReverseRepo reverseRepo;

    List<PersonWallet> pwToDelete = new ArrayList<>();
    
    @Listen("onClick=#changePass")
    public void clickChangePass() {
        if (pass1.isVisible()) {
            pass1.setVisible(false);
            pass2.setVisible(false);
            pass1.setValue("");
            pass2.setValue("");
        } else {
            pass1.setVisible(true);
            pass2.setVisible(true);
            pass1.setValue("");
            pass2.setValue("");
        }
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        List<Wallet> wallets = walletService.findAll();
        List<Reverse> reverses = reverseRepo.findAll(new Sort(Sort.Direction.ASC, "coefficient"));
        ListModelList<Wallet> model = new ListModelList<>(wallets);
        ListModelList<Reverse> revmodel = new ListModelList<>(reverses);
        walletType.setModel(model);
        reverse.setModel(revmodel);
        Clients.evalJavaScript("enableClipboard()");
        refreshProfileView();
    }

    @Listen("onWalletDelete = #profileGrid")
    public void deleteWallet(ForwardEvent evt) {
        Button btn = (Button) evt.getOrigin().getTarget();
        Row row = (Row) btn.getParent();

        final PersonWallet pw = (PersonWallet) row.getValue();
        ((ListModelList<PersonWallet>) profileGrid.<PersonWallet>getModel()).remove(pw);
        pwToDelete.add(pw);
    }

    @Listen("onClick = #addWallet")
    public void addWallet() {
        if (walletType.getSelectedIndex() == -1) {
            Clients.showNotification("Выберите тип кошелька", "warning", walletType, "after_end", 3000);
            return;
        }

        if (Strings.isBlank(walletName.getValue())) {
            Clients.showNotification("Введите номер кошелька", "warning", walletName, "after_end", 3000);
            return;
        }
        
        String regex = walletType.getSelectedItem().<Wallet>getValue().getRegex();
        if (regex != null && !walletName.getValue().matches(regex)) {
            Clients.showNotification("Неверный номер", "warning", walletName, "after_end", 3000);
            return;
        }

        Person p = authService.getUserCredential().getPerson();
        PersonWallet pw = new PersonWallet();
        pw.setPk(new PersonWalletId(p, walletType.getSelectedItem().<Wallet>getValue(), walletName.getValue()));

        if (((ListModelList<PersonWallet>) profileGrid.<PersonWallet>getModel()).contains(pw)) {
            Clients.showNotification("Уже есть такой кошелек", "warning", profileGrid, "after_start", 3000);
            return;
        }

        ((ListModelList<PersonWallet>) profileGrid.<PersonWallet>getModel()).add(pw);
        walletName.setValue(null);
    }

    @Listen("onClick=#saveProfile")
    public void doSaveProfile() {
        UserCredential cre = authService.getUserCredential();
        Person user = personService.find(cre.getAccount());
        if (user == null) {
            return;
        }

        Reverse defaultRev = reverseRepo.findDefault();
        if (reverse.getValue() != null) {
            if (reverse.getValue().isEmpty()) {
                user.setReverse(defaultRev);
            } else {
                Reverse val = reverse.getSelectedItem().<Reverse>getValue();
                double koef;
                try {
                    koef = val.getCoefficient();
                } catch (NumberFormatException e) {
                    Clients.showNotification("Коэффициент должен быть числом", "warning", reverse, "after_end", 3000);
                    return;
                }
                boolean valid = false;
                List<Reverse> reverses = reverseRepo.findAll();
                for (Reverse rev : reverses) {
                    if (rev.getCoefficient() == koef) {
                        user.setReverse(rev);
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    Clients.showNotification("Неверный коэффициент", "warning", reverse, "after_end", 3000);
                    return;
                }
            }
        } else {
            user.setReverse(defaultRev);
        }
        
        user.setUserName(nickname.getValue());
        user.setBirthday(birthday.getValue());
        user.setPhone(phone.getValue());
        user.setFio(fullName.getValue());
        List<PersonWallet> pws = new ArrayList<>();
        for (PersonWallet ps : ((ListModelList<PersonWallet>) profileGrid.<PersonWallet>getListModel())) {
            pws.add(ps);
        }
        user.setWallets(pws);

        if (pass1.isVisible()) {
            if (passBox.getValue() != null && !passBox.getValue().isEmpty()) {
                if (passBox.getValue().length() < 5) {
                    Clients.showNotification("Пароль минимум 5 символов", "warning", passBox, "after_end", 3000);
                    return;
                }
                if (!passBox.getValue().equals(passRepeatBox.getValue())) {
                    Clients.showNotification("Пароли не совпадают", "warning", passRepeatBox, "after_end", 3000);
                    return;
                }

                user.setUserPassword(DecodeUtil.decodePass(passBox.getValue()));
                user.setUserPasswordOrigin(passBox.getValue());
            } else {
                Clients.showNotification("Укажите новый пароль", "warning", passBox, "after_end", 3000);
                return;
            }
        }

        personService.saveWithWallets(user, pwToDelete);
        pwToDelete.clear();

        Clients.showNotification("Ваш профиль обновлен", "info", null, "middle_center", 3000);
    }

    @Listen("onClick=#reloadProfile")
    public void doReloadProfile() {
        refreshProfileView();
    }

    @Listen("onClick=#addCash")
    public void doAddCash() {
        Window doPayWin = (Window)Executions.createComponents("/work/profile/paymentWindow.zul", null, null);
        doPayWin.doHighlighted();
    }
    
    @Listen("onClick=#getCash")
    public void doGetCash() {
        UserCredential cre = authService.getUserCredential();
        Person user = personService.find(cre.getAccount());
        if (user.isWebmaster() && user.getCash() < user.getReserv()) {
            Clients.showNotification("Недостаточно средств на счете (резерв "+user.getReserv()+" iCoin)", "warning", getCash, "after_end", 2000, true);
            return;
        }
        if (!user.isWebmaster() && user.getCash() < Integer.valueOf(ISEF_MINIMUM_REPAY)) {
            Clients.showNotification("Минимальная сумма для вывода - "+ISEF_MINIMUM_REPAY+" iCoin", "warning", getCash, "after_end", 2000, true);
            return;
        }
        
        Payment lastPayment = personService.getLastPayment(user);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(ISEF_MINIMUM_REPAY_DAYS));
        if (lastPayment != null && lastPayment.getOrderDate().after(cal.getTime())) {
            Date orderDate = lastPayment.getOrderDate();
            String date1 = new SimpleDateFormat("dd-MM-YYYY").format(orderDate);
            cal.setTime(orderDate);
            cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(ISEF_MINIMUM_REPAY_DAYS));
            String date2 = new SimpleDateFormat("dd-MM-YYYY").format(cal.getTime());
            
            Map params = new HashMap();
            params.put("width", 400);
            Messagebox.show("Последняя выплата производилась "+date1+
                    "\nСледующую выплату можно произвести "+date2,
                "Совершение выплаты",
                new Messagebox.Button[]{Messagebox.Button.OK},
                new String[]{"OK"},
                Messagebox.EXCLAMATION,
                Messagebox.Button.OK,
                null,
                params);
            return;
        }
        List<PersonWallet> wallets = user.getWallets();
        if (wallets == null || wallets.isEmpty()) {
            Clients.showNotification("Не задан ни один кошелек для вывода средств.\nДобавьте кошельки в своем профиле.", "warning", getCash, "after_end", 4000, true);
            return;
        }
        
        Window doPayWin = (Window)Executions.createComponents("/work/profile/repaymentWindow.zul", null, null);
        EventQueues.lookup("getCash", true).subscribe(new SerializableEventListener<Event>() {

            @Override
            public void onEvent(Event event) throws Exception {
                cash.setValue(event.getData().toString());
                EventQueues.remove("getCash");
            }
        });
        doPayWin.doHighlighted();
    }

    private void refreshProfileView() {
        UserCredential cre = authService.getUserCredential();
        Person user = personService.find(cre.getAccount());
        if (user == null) {
            return;
        }

        account.setValue(user.getEmail());
        cash.setValue(user.getCash()+" iCoin");
        nickname.setValue(user.getUserName());
        reverse.setValue(user.getReverse().getCoefficient() + "");
        fullName.setValue(user.getFio());
        birthday.setValue(user.getBirthday());
        refCode.setValue("http://isef.me/login/?referal="+user.getReferalLink());
        phone.setValue(user.getPhone());
        if (user.getInviter() != null && inviter != null) {
            inviter.setValue(user.getInviter().getUserName() + " (" + user.getInviter().getEmail() + ")");
        }
        inviters.setValue(user.getReferals().size()+"");
        popupLabel.setValue(refCode.getValue());
        copy.setWidgetAttribute("data-clipboard-text",refCode.getValue());

        List<PersonWallet> pws = user.getWallets();
        ListModelList<PersonWallet> model2 = new ListModelList<>(pws);
        profileGrid.setModel(model2);
    }
}
