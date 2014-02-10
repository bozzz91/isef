package ru.desu.home.isef.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;
import ru.desu.home.isef.entity.ActivationPerson;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Role;
import ru.desu.home.isef.services.ActivationPersonService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.utils.GoogleMail;
import ru.desu.home.isef.utils.DecodeUtil;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LoginController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;
    private static final String ADMIN_EMAIL = "bozzz91";
    private static final String ADMIN_PASS = "cnfhsqyzrj";
    private static final String ADMIN_EMAIL_TITLE = "ISef Registration";
    private static final String HOST_LINK = "localhost:8080";
    private static final String HOST_APP = "work";

    //win
    @Wire
    Window loginWin;

    //login
    @Wire
    Textbox account;
    @Wire
    Textbox password;
    @Wire
    Label message;
    @Wire
    Vbox loginLay;

    //registr
    @Wire
    Vbox regLay;
    @Wire
    Button resetButton;
    @Wire
    Button submitButton;
    @Wire
    Button cancelButton;
    @Wire
    Checkbox acceptTermBox;
    @Wire
    Textbox fullnameBox;
    @Wire
    Textbox refBox;
    @Wire
    Textbox nicknameBox;
    @Wire
    Textbox emailBox;
    @Wire
    Textbox phoneBox;
    @Wire
    Textbox passBox;
    @Wire
    Textbox passRepeatBox;
    @Wire
    Datebox birthdayBox;
    @Wire
    Radiogroup genderRadio;

    @WireVariable
    AuthenticationService authService;

    @WireVariable
    ActivationPersonService activationService;

    @WireVariable
    PersonService personService;

    @Listen("onClick=#login; onOK=#loginWin")
    public void doLogin() {
        String nm = account.getValue();
        String pd = password.getValue();

        if (!authService.login(nm, pd)) {
            message.setValue("Неверные e-mail или пароль.");
            return;
        }
        message.setSclass("");

        Executions.sendRedirect("/work/");
    }

    @Listen("onClick=#reg")
    public void doOpenReg() {
        loginWin.setTitle("Регистрация");
        loginLay.setVisible(false);
        regLay.setVisible(true);
    }

    @Listen("onClick=#cancelButton")
    public void doCancelReg() {
        loginWin.setTitle("Авторизация");
        regLay.setVisible(false);
        loginLay.setVisible(true);
    }

    @Listen("onClick=#submitButton")
    public void doReg() {
        final String addr = emailBox.getValue();
        Person existPerson = personService.find(addr);
        if (existPerson != null) {
            Messagebox.show("Указанный e-mail уже занят", "Error", Messagebox.OK, Messagebox.ERROR);
            emailBox.focus();
            return;
        }

        Person inviter = null;
        if (refBox.getValue() != null && !refBox.getValue().isEmpty()) {
            inviter = personService.findByRefCode(refBox.getValue());
            if (inviter == null) {
                //Messagebox.show("Неверный реферальный код. Такого пользователя не существует", "Error", Messagebox.OK, Messagebox.ERROR);
                Clients.showNotification("Неверный реферальный код. Такого пользователя не существует", "error", refBox, "after_end", 5000);
                refBox.focus();
                return;
            }
        }

        Person p = new Person();
        p.setActive(false);
        p.setCash(0d);
        p.setEmail(emailBox.getValue());
        p.setFio(fullnameBox.getValue());
        p.setPhone(phoneBox.getValue());
        p.setUserName(nicknameBox.getValue());
        p.setUserPassword(passBox.getValue());
        p.setInviter(inviter);
        p.setBirthday(birthdayBox.getValue());

        Role r = personService.findRole(Role.Roles.USER);
        p.setRole(r);

        final String code = DecodeUtil.decodeEmail(addr);
        p.setReferalLink(code);

        ActivationPerson ap = new ActivationPerson();
        ap.setCode(code);
        ap.setEmail(addr);

        ap = activationService.save(ap);
        personService.save(p);
        final Long id = ap.getId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder msg = new StringBuilder("Hello ");
                    msg.append(nicknameBox.getValue()).append("!\nYour activation code is: ")
                            .append(code).append("\nYour activation link: ")
                            .append("<a href=\"http://").append(HOST_LINK)
                            .append("/activation.zul?code=").append(code).append("&id=")
                            .append(id).append("\"> Click Here</a>");
                    GoogleMail.Send(ADMIN_EMAIL, ADMIN_PASS, addr, ADMIN_EMAIL_TITLE, msg.toString());
                } catch (MessagingException ex) {
                    log.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }).start();

        Messagebox.show("На указанный e-mail отправлено письмо с указаниями для завершения регистрации",
                "Info",
                Messagebox.OK,
                Messagebox.NONE,
                new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        Executions.sendRedirect("/");
                    }
                });
    }

    @Listen("onCheck = #acceptTermBox")
    public void changeSubmitStatus() {
        if (acceptTermBox.isChecked()) {
            for (Component c : loginWin.getFellows()) {
                if (c instanceof InputElement && !((InputElement) c).isValid()) {
                    acceptTermBox.setChecked(false);
                    ((InputElement) c).focus();
                    Clients.showNotification("Заполните все необходимые для регистрации поля");
                    return;
                }
            }
            if (passBox.getValue().length() < 5) {
                acceptTermBox.setChecked(false);
                Clients.showNotification("Короткий пароль, минимум 5 символов");
                passBox.focus();
                return;
            }
            if (!passBox.getValue().equals(passRepeatBox.getValue()) || passBox.getValue().length() < 5) {
                acceptTermBox.setChecked(false);
                Clients.showNotification("Пароли не совпадают");
                passBox.focus();
                return;
            }

            submitButton.setDisabled(false);
            submitButton.setImage("/imgs/ok.png");
        } else {
            submitButton.setDisabled(true);
            submitButton.setImage("");
        }
    }
    
    @Listen("onBlur = #passBox")
    public void blurPassBox() {
        if (passBox.getValue() != null && passBox.getValue().length() < 5) {
            throw new WrongValueException(passBox, "Минимум 5 символов");
        }
    }
    
    @Listen("onBlur = #passRepeatBox")
    public void blurPassRepeatBox() {
        if (passRepeatBox.getValue() != null && !passRepeatBox.getValue().equals(passBox.getValue())) {
            throw new WrongValueException(passRepeatBox, "Пароли не совпадают");
        }
    }
}
