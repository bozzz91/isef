package ru.desu.home.isef.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.zkoss.zul.Popup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.PersonWallet;
import ru.desu.home.isef.entity.PersonWalletId;
import ru.desu.home.isef.entity.Wallet;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.WalletService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.services.auth.UserCredential;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProfileViewController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    //wire components
    @Wire
    Label account, cash, refCode, inviter;
    @Wire
    Textbox passBox, passRepeatBox, nickname, fullName, phone, walletName;
    @Wire
    Datebox birthday;
    @Wire
    Button changePass;
    @Wire
    Row pass1, pass2;
    @Wire
    Combobox walletType;
    @Wire
    Grid profileGrid;

    @WireVariable
    AuthenticationService authService;
    @WireVariable
    PersonService personService;
    @WireVariable
    WalletService walletService;

    Window doPayWin;
    Set<PersonWallet> pwToDelete = new HashSet<>();
    
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
        ListModelList<Wallet> model = new ListModelList<>(wallets);
        walletType.setModel(model);
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

        user.setUserName(nickname.getValue());
        user.setBirthday(birthday.getValue());
        user.setPhone(phone.getValue());
        Set<PersonWallet> pws = new HashSet<>();
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

                user.setUserPassword(passBox.getValue());
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
        doPayWin = (Window)Executions.createComponents("/work/profile/paymentWindow.zul", null, null);
        EventQueues.lookup("cash", true).subscribe(new SerializableEventListener<Event>() {

            @Override
            public void onEvent(Event event) throws Exception {
                cash.setValue(event.getData().toString());
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
        cash.setValue(user.getCash().toString());
        nickname.setValue(user.getUserName());
        fullName.setValue(user.getFio());
        birthday.setValue(user.getBirthday());
        refCode.setValue(user.getReferalLink());
        phone.setValue(user.getPhone());
        if (user.getInviter() != null && inviter != null) {
            inviter.setValue(user.getInviter().getUserName() + " (" + user.getInviter().getEmail() + ")");
        }

        Set<PersonWallet> pws = user.getWallets();
        ListModelList<PersonWallet> model2 = new ListModelList<>(pws);
        profileGrid.setModel(model2);
    }
}
