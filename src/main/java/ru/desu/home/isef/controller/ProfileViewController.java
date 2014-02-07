package ru.desu.home.isef.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
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
    Textbox fullName;
    @Wire
    Textbox email;
    @Wire
    Datebox birthday;
    @Wire
    Listbox country;
    @Wire
    Textbox bio;
    @Wire
    Label nameLabel;

    @WireVariable
    AuthenticationService authService;
    @WireVariable
    PersonService personService;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        ListModelList<String> countryModel = new ListModelList<>();
        country.setModel(countryModel);

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
        user.setUserName(fullName.getValue());
        user.setEmail(email.getValue());
        //user.setBirthday(birthday.getValue());
        //user.setBio(bio.getValue());

        /*Set<String> selection = ((ListModelList) country.getModel()).getSelection();
         if (!selection.isEmpty()) {
         user.setCountry(selection.iterator().next());
         } else {
         user.setCountry(null);
         }*/
        nameLabel.setValue(fullName.getValue());

        personService.save(user);

        Clients.showNotification("Your profile is updated");
    }

    @Listen("onClick=#reloadProfile")
    public void doReloadProfile() {
        refreshProfileView();
    }

    private void refreshProfileView() {
        UserCredential cre = authService.getUserCredential();
        Person user = personService.find(cre.getAccount());
        if (user == null) {
            //TODO handle un-authenticated access 
            return;
        }

        //apply bean value to UI components
        account.setValue(user.getUserName());
        fullName.setValue(user.getFio());
        email.setValue(user.getEmail());

        //((ListModelList) country.getModel()).addToSelection(user.getCountry());
        nameLabel.setValue(user.getUserName());
    }
}
