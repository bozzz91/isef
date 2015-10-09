package ru.desu.home.isef.controller.login;

import lombok.extern.java.Log;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

    //registration
    @Wire Vbox regLay;
    @Wire Button submitButton;
    @Wire Checkbox acceptTermBox;
    @Wire Textbox fullNameBox, refBox, nicknameBox, emailBox, phoneBox, passBox, passRepeatBox;
    @Wire Datebox birthdayBox;
    @Wire Radiogroup webmaster, sex;

	//restore
	@Wire Vbox restoreLay;
	@Wire Textbox restoreEmail;

    @WireVariable AuthenticationService authService;
    @WireVariable ActivationPersonService activationService;
    @WireVariable PersonService personService;
	@WireVariable ConfigUtil config;
	@WireVariable MailUtil mail;

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
        String nm = account.getValue().trim();
        String pd = password.getValue().trim();

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

    @Listen("onClick = #reg")
    public void doOpenReg() {
        loginWin.setTitle("Регистрация");
        loginLay.setVisible(false);
		restoreLay.setVisible(false);
        regLay.setVisible(true);
    }

    @Listen("onClick = #cancelButton")
    public void doCancelReg() {
        loginWin.setTitle("Авторизация");
        regLay.setVisible(false);
		restoreLay.setVisible(false);
		loginLay.setVisible(true);
    }

	@Listen("onClick = #restorePass")
	public void openRestoreLayout() {
		loginWin.setTitle("Восстановление");
		regLay.setVisible(false);
		restoreLay.setVisible(true);
		loginLay.setVisible(false);
	}

	@Listen("onClick = #restore")
	public void restorePass() {
		Person existPerson = personService.find(restoreEmail.getValue());
		if (existPerson == null) {
			Clients.showNotification("Такого e-mail нет в системе");
			return;
		}

		new Thread(() -> {
			try {
				String newPass = new Random().ints(8, 0, 10).mapToObj(String::valueOf).reduce("", (a, b) -> (a + b));
				Map<String, String> params = new HashMap<>();
				params.put("pass", newPass);
				mail.send(restoreEmail.getValue(), MailUtil.MailType.RESTORE, params);
				existPerson.setUserPassword(DecodeUtil.decodePass(newPass));
				existPerson.setUserPasswordOrigin(newPass);
				personService.save(existPerson);
			} catch (Exception ex) {
				log.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}).start();

		Messagebox.show("Новый пароль выслан на указанный e-mail",
				"Info",
				Messagebox.OK,
				Messagebox.NONE,
				event -> {
					doCancelReg();
				}
		);
	}

	@Listen("onClick = #cancelRestore")
	public void closeRestoreLayout() {
		doCancelReg();
	}

    @Listen("onClick=#submitButton")
    public void doReg() {
        final String address = emailBox.getValue();
        Person existPerson = personService.find(address);
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
                Clients.showNotification("Неверный реферальный код. Такого пользователя не существует", "error", refBox, "after_end", 5000);
                return;
            }
        }

        Person p = new Person();
        p.setActive(false);
        p.setCash(0d);
        p.setEmail(emailBox.getValue());
        p.setFio(fullNameBox.getValue());
        p.setPhone(phoneBox.getValue());
        p.setUserName(nicknameBox.getValue());
        p.setUserPassword(DecodeUtil.decodePass(passBox.getValue().trim()));
		//save origin pass only for app developer
        if (config.isSaveOriginPassword()) {
			p.setUserPasswordOrigin(passBox.getValue().trim());
		}
        p.setInviter(inviter);
        p.setBirthday(birthdayBox.getValue());
        p.setWebmaster(webmaster.getSelectedIndex() == 0);
        p.setSex(sex.getSelectedIndex() == 0 ? "М" : "Ж");

        Role r = personService.findRole(Role.Roles.USER);
        p.setRole(r);

        final String code = DecodeUtil.decodeEmail(address);
        p.setReferalLink(code);

        ActivationPerson ap = new ActivationPerson();
        ap.setCode(code);
        ap.setEmail(address);

        ap = activationService.save(ap);
        personService.save(p);
        final Long id = ap.getId();
		final String serverName = Executions.getCurrent().getServerName();
        new Thread(() -> {
			try {
				Map<String, String> params = new HashMap<>();
				params.put("nick", nicknameBox.getValue());
				params.put("code", code);
				params.put("server", serverName);
				params.put("id", String.valueOf(id));
				mail.send(address, MailUtil.MailType.REGISTRATION, params);
			} catch (WrongValueException | MessagingException ex) {
				log.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}).start();

        Messagebox.show("На указанный e-mail отправлено письмо с указаниями для завершения регистрации",
                "Info",
                Messagebox.OK,
                Messagebox.NONE,
				event -> {
					Executions.sendRedirect("/");
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
