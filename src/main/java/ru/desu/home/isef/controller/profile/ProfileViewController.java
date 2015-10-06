package ru.desu.home.isef.controller.profile;

import lombok.extern.java.Log;
import org.zkoss.image.AImage;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import ru.desu.home.isef.entity.*;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.WalletService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.services.auth.UserCredential;
import ru.desu.home.isef.utils.ConfigUtil;
import ru.desu.home.isef.utils.DecodeUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProfileViewController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;
    
    //wire components
    @Wire Label account, cash, inviter, inviters, popupLabel, ratingPopupLabel, rating, reverse;
    @Wire Textbox passBox, passRepeatBox, nickname, fullName, phone, walletName, refCode;
    @Wire Datebox birthday;
    @Wire Button changePass, getCash, copy;
    @Wire Row pass1, pass2;
    @Wire Combobox walletType; //, reverse (ComboBox version);
    @Wire Grid profileGrid;
	@Wire Image img;

    @WireVariable AuthenticationService authService;
    @WireVariable PersonService personService;
    @WireVariable WalletService walletService;
	@WireVariable ConfigUtil config;

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
        ListModelList<Wallet> model = new ListModelList<>(wallets);
        walletType.setModel(model);
        
        Clients.evalJavaScript("enableClipboard()");
        refreshProfileView();
    }

    @Listen("onWalletDelete = #profileGrid")
    public void deleteWallet(ForwardEvent evt) {
        Button btn = (Button) evt.getOrigin().getTarget();
        Row row = (Row) btn.getParent();

        final PersonWallet pw = row.getValue();
        ((ListModelList<PersonWallet>) profileGrid.<PersonWallet>getModel()).remove(pw);
        pwToDelete.add(pw);
    }

	@Listen("onUpload= #upload")
	public void onPhotoUpload(UploadEvent event) throws IOException {
		byte[] bytes = event.getMedia().getByteData();
		img.setContent(new AImage("ava",bytes));
		UserCredential cre = authService.getUserCredential();
		Person user = personService.find(cre.getAccount());
		user.setPhoto(bytes);
		personService.save(user);
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
		Integer minimumRepay = config.getMinimumRepay();
        if (!user.isWebmaster() && user.getCash() < minimumRepay) {
            Clients.showNotification("Минимальная сумма для вывода - "+ minimumRepay +" iCoin", "warning", getCash, "after_end", 2000, true);
            return;
        }
        
        Payment lastPayment = personService.getLastPayment(user);
        Calendar cal = Calendar.getInstance();
		Integer repayInterval = config.getRepayInterval();
        cal.add(Calendar.DAY_OF_MONTH, -repayInterval);
        if (lastPayment != null && lastPayment.getOrderDate().after(cal.getTime())) {
            Date orderDate = lastPayment.getOrderDate();
            String date1 = new SimpleDateFormat("dd-MMM-YYYY").format(orderDate);
            cal.setTime(orderDate);
            cal.add(Calendar.DAY_OF_MONTH, repayInterval);
            String date2 = new SimpleDateFormat("dd-MMM-YYYY").format(cal.getTime());
            
            Map<String, String> params = new HashMap<>();
            params.put("width", "400");
            Messagebox.show("Последняя выплата производилась " + date1 +
							"\nСледующую выплату можно произвести " + date2,
					"Совершение выплаты",
					new Messagebox.Button[] {Messagebox.Button.OK},
					new String[] {"OK"},
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
		cash.setValue(user.getCash() + " iCoin");
		Rating rate = personService.getRating(user);
		rating.setValue(user.getRating() + " (" + rate.getName() + ")");
		Rating nextRate = personService.getNextRating(rate);
		String ratePopup;
		if (nextRate != null) {
			ratePopup = "Баллов до повышения осталось: " + (nextRate.getPoints() - user.getRating());
		} else {
			ratePopup = "У вас максимальный статус!";
		}
		ratingPopupLabel.setValue(ratePopup);
		nickname.setValue(user.getUserName());

		reverse.setValue((int)(personService.getRating(user).getReverse()*100) + "%");

		fullName.setValue(user.getFio());
		birthday.setValue(user.getBirthday());

		byte [] photo = cre.getPerson().getPhoto();
		if (photo == null) {
			photo = user.getPhoto();
			cre.getPerson().setPhoto(photo);
		}
		String defaultPhotoPath = "/imgs/no-ava.png";
		if (photo == null) {
			img.setSrc(defaultPhotoPath);
		} else {
			try {
				img.setContent(new AImage("ava", photo));
			} catch (Exception e) {
				img.setSrc(defaultPhotoPath);
			}
		}
        refCode.setValue("http://" + Executions.getCurrent().getServerName() + "/login/?referal="+user.getReferalLink());
        phone.setValue(user.getPhone());
        if (user.getInviter() != null && inviter != null) {
            inviter.setValue(user.getInviter().getUserName() + " (" + user.getInviter().getEmail() + ")");
        }
        inviters.setValue(user.getReferals().size()+"");
        popupLabel.setValue(refCode.getValue());
        copy.setWidgetAttribute("data-clipboard-text", refCode.getValue());

        List<PersonWallet> pws = user.getWallets();
        ListModelList<PersonWallet> model2 = new ListModelList<>(pws);
        profileGrid.setModel(model2);
    }
}
