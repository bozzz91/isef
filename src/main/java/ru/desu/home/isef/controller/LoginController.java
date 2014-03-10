package ru.desu.home.isef.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import javax.mail.MessagingException;
import lombok.extern.java.Log;
import org.springframework.util.StringUtils;
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
    private static final String ADMIN_EMAIL;
    private static final String ADMIN_PASS;
    private static final String ADMIN_EMAIL_TITLE;
    private static final String HOST_LINK;

    static {
        Properties props = new Properties();
        try {
            props.load(LoginController.class.getResourceAsStream("/config.txt"));
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        ADMIN_EMAIL = props.getProperty("admin_email");
        ADMIN_PASS = props.getProperty("admin_pass");
        ADMIN_EMAIL_TITLE = props.getProperty("admin_email_title");
        HOST_LINK = props.getProperty("host_link");

        ArrayList<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(ADMIN_EMAIL)) {
            errors.add("admin_email");
        }
        if (StringUtils.isEmpty(ADMIN_EMAIL_TITLE)) {
            errors.add("admin_email_title");
        }
        if (StringUtils.isEmpty(ADMIN_PASS)) {
            errors.add("admin_pass");
        }
        if (StringUtils.isEmpty(HOST_LINK)) {
            errors.add("host_link");
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Неверные параметры " + Arrays.toString(errors.toArray()) + " в config.txt");
        }
    }

    //win
    @Wire
    Window loginWin;

    //login
    @Wire
    Textbox account, password;
    @Wire
    Label message, refBoxP, popupLabel;
    @Wire
    Vbox loginLay;

    //registr
    @Wire
    Vbox regLay;
    @Wire
    Button resetButton, submitButton, cancelButton;
    @Wire
    Checkbox acceptTermBox;
    @Wire
    Textbox fullnameBox, refBox, nicknameBox, emailBox, phoneBox, passBox, passRepeatBox;
    @Wire
    Datebox birthdayBox;
    @Wire
    Radiogroup webmaster;

    @WireVariable
    AuthenticationService authService;
    @WireVariable
    ActivationPersonService activationService;
    @WireVariable
    PersonService personService;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Clients.evalJavaScript("restore()");
        String paramRef = Executions.getCurrent().getParameter("referal");
        if (!Strings.isBlank(paramRef)) {
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

        if (webmaster.getSelectedIndex() == -1) {
            Clients.showNotification("Выберите тип аккаунта", "error", webmaster, "after_end", 5000);
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
