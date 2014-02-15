package ru.desu.home.isef.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.services.auth.UserCredential;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProfileViewController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    //wire components
    @Wire
    Label account;
    @Wire
    Label cash;
    @Wire
    Label refCode;
    @Wire
    Label inviter;
    @Wire
    Textbox fullName;
    @Wire
    Textbox nickname;
    @Wire
    Datebox birthday;
    //@Wire
    //Listbox country;
    @Wire
    Textbox phone;
    
    @Wire
    Button changePass;
    @Wire
    Textbox passBox;
    @Wire
    Textbox passRepeatBox;
    @Wire
    Row pass1;
    @Wire
    Row pass2;

    @WireVariable
    AuthenticationService authService;
    @WireVariable
    PersonService personService;

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

        //ListModelList<String> countryModel = new ListModelList<>();
        //country.setModel(countryModel);

        refreshProfileView();
    }

    @Listen("onClick=#saveProfile")
    public void doSaveProfile() {
        UserCredential cre = authService.getUserCredential();
        Person user = personService.find(cre.getAccount());
        if (user == null) {
            //TODO handle un-authenticated access 
            return;
        }

        //apply component value to bean
        user.setUserName(nickname.getValue());
        //user.setEmail(email.getValue());
        user.setBirthday(birthday.getValue());
        user.setPhone(phone.getValue());
        //user.setBio(bio.getValue());

        /*Set<String> selection = ((ListModelList) country.getModel()).getSelection();
         if (!selection.isEmpty()) {
         user.setCountry(selection.iterator().next());
         } else {
         user.setCountry(null);
         }*/
        
        if (pass1.isVisible()) {
            if (passBox.getValue() != null && !passBox.getValue().isEmpty()) {
                if (passBox.getValue().length() < 5) {
                    Clients.showNotification("Пароль минимум 5 символов");
                    return;
                }
                if (!passBox.getValue().equals(passRepeatBox.getValue())) {
                    Clients.showNotification("Пароли не совпадают");
                    return;
                }
                
                user.setUserPassword(passBox.getValue());
            } else {
                Clients.showNotification("Укажите новый пароль");
                return;
            }
        }
        
        personService.save(user);

        Clients.showNotification("Ваш профиль обновлен");
    }

    @Listen("onClick=#reloadProfile")
    public void doReloadProfile() {
        refreshProfileView();
    }
    
    @Listen("onClick=#addCash")
    public void doAddCash() {
        Messagebox.show("Не реализовано пока, просто добавим пользователю 10", "Пополнение баланса", Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Clients.showNotification("Добавили 10 единиц", "info", cash, "after_end", 1000);
                Person p = authService.getUserCredential().getPerson();
                p = personService.findById(p.getId());
                p.setCash(p.getCash()+10);
                personService.save(p);
                authService.getUserCredential().setPerson(p);
                cash.setValue(p.getCash().toString());
            }
        });
    }

    private void refreshProfileView() {
        UserCredential cre = authService.getUserCredential();
        Person user = personService.find(cre.getAccount());
        if (user == null) {
            //TODO handle un-authenticated access 
            return;
        }
        
        //apply bean value to UI components
        account.setValue(user.getEmail());
        cash.setValue(user.getCash().toString());
        nickname.setValue(user.getUserName());
        fullName.setValue(user.getFio());
        birthday.setValue(user.getBirthday());
        refCode.setValue(user.getReferalLink());
        phone.setValue(user.getPhone());
        if (user.getInviter() != null) {
            inviter.setValue(user.getInviter().getUserName() + " ("+user.getInviter().getEmail()+")");
        }

        //((ListModelList) country.getModel()).addToSelection(user.getCountry());
    }
}
