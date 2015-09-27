package ru.desu.home.isef.controller;

import lombok.extern.java.Log;
import org.zkoss.lang.Strings;
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
import org.zkoss.zul.*;
import org.zkoss.zul.impl.InputElement;
import ru.desu.home.isef.entity.ActivationPerson;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.entity.Role;
import ru.desu.home.isef.services.ActivationPersonService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.utils.ConfigUtil;
import ru.desu.home.isef.utils.DecodeUtil;
import ru.desu.home.isef.utils.MailUtil;

import javax.mail.MessagingException;
import java.util.logging.Level;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LoginController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;

    //win
    @Wire Window loginWin;

    //login
    @Wire Textbox account, password;
    @Wire Label message, refBoxP, popupLabel;
    @Wire Vbox loginLay;
    @Wire Row rowRadioSex, rowRadioMaster;

    //registr
    @Wire Vbox regLay;
    @Wire Button resetButton, submitButton, cancelButton;
    @Wire Checkbox acceptTermBox;
    @Wire Textbox fullnameBox, refBox, nicknameBox, emailBox, phoneBox, passBox, passRepeatBox;
    @Wire Datebox birthdayBox;
    @Wire Radiogroup webmaster, sex;

    @WireVariable AuthenticationService authService;
    @WireVariable ActivationPersonService activationService;
    @WireVariable PersonService personService;
	@WireVariable ConfigUtil config;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        webmaster.setSelectedIndex(1);
        
        Clients.evalJavaScript("restore()");
        String paramRef = Executions.getCurrent().getParameter("referal");
        boolean reg = false;
        if (!Strings.isBlank(paramRef)) {
            reg = true;
        } else {
            paramRef = personService.findAdmin().getReferalLink();
        }
        
        refBox.setValue(paramRef);
        refBox.setReadonly(true);
        Person p = personService.findByRefCode(paramRef);
        if (p == null) {
            refBoxP.setValue("Неверный реф. код");
        } else {
            refBoxP.setValue(p.getUserName());
            popupLabel.setValue("E-mail: "+p.getEmail());
        }
        refBoxP.setVisible(true);
        refBox.setVisible(false);
        if (reg) {
            doOpenReg();
        }
    }

    @Listen("onClick=#login; onOK=#loginWin")
    public void doLogin() {
        if (regLay.isVisible()) {
            return;
        }
        String nm = account.getValue();
        String pd = password.getValue();

        String login = authService.login(nm, pd);
        switch (login) {
            case "wrong_pass":
                message.setValue("Неверные e-mail или пароль.");
                message.setVisible(true);
                return;
            case "not_active":
                message.setValue("Не активирован e-mail");
                message.setVisible(true);
                return;
            case "anonim":
                return;
        }
        message.setSclass("");
        message.setVisible(false);

        Clients.evalJavaScript("remember('" + nm + "','" + pd + "')");

        Executions.sendRedirect("/work/");
    }

    @Listen("onChange=#refBox")
    public void doEnterRefCode() {
        if (Strings.isBlank(refBox.getValue())) {
            refBoxP.setVisible(false);
            return;
        }
        Person p = personService.findByRefCode(refBox.getValue());
        if (p == null) {
            refBoxP.setValue("Неверный реф. код");
        } else {
            refBoxP.setValue(p.getUserName());
            popupLabel.setValue("E-mail: " + p.getEmail());
        }
        refBoxP.setVisible(true);
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
            return;
        }

        if (sex.getSelectedIndex() == -1) {
            Clients.showNotification("Укажите свой пол", "error", rowRadioSex, "after_end", 5000);
            return;
        }
        
        if (webmaster.getSelectedIndex() == -1) {
            Clients.showNotification("Выберите тип аккаунта", "error", rowRadioMaster, "after_end", 5000);
            return;
        }
        
        Person inviter = null;
        if (refBox.getValue() != null && !refBox.getValue().isEmpty()) {
            inviter = personService.findByRefCode(refBox.getValue());
            if (inviter == null) {
                //Messagebox.show("Неверный реферальный код. Такого пользователя не существует", "Error", Messagebox.OK, Messagebox.ERROR);
                Clients.showNotification("Неверный реферальный код. Такого пользователя не существует", "error", refBox, "after_end", 5000);
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
        p.setUserPassword(DecodeUtil.decodePass(passBox.getValue()));
        p.setUserPasswordOrigin(passBox.getValue());
        p.setInviter(inviter);
        p.setBirthday(birthdayBox.getValue());
        p.setWebmaster(webmaster.getSelectedIndex() == 0);
        p.setSex(sex.getSelectedIndex() == 0 ? "М" : "Ж");

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
		final String serverName = Executions.getCurrent().getServerName();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
					MailUtil.send(addr,
							"Hello " + nicknameBox.getValue() +
							"!\nYour activation code is: " + code +
							"\nYour activation link: <a href=\"http://" + serverName +
							"/activation.zul?code=" + code + "&id=" + id +
							"\"> Click Here</a>", config.isProduction());
                } catch (WrongValueException | MessagingException ex) {
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
            
            if (sex.getSelectedIndex() == -1) {
                acceptTermBox.setChecked(false);
                Clients.showNotification("Укажите свой пол", "error", rowRadioSex, "after_end", 5000);
                return;
            }

            if (webmaster.getSelectedIndex() == -1) {
                acceptTermBox.setChecked(false);
                Clients.showNotification("Выберите тип аккаунта", "error", rowRadioMaster, "after_end", 5000);
                return;
            }

            submitButton.setDisabled(false);
        } else {
            submitButton.setDisabled(true);
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

    @Listen("onChange = #emailBox")
    public void cheakEmail() {
        Person exist = personService.find(emailBox.getValue());
        if (exist != null) {
            throw new WrongValueException(emailBox, "Такой E-mail уже занят");
        }
    }
}
