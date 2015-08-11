package ru.desu.home.isef.controller;

import lombok.extern.java.Log;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.BannerService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.utils.ConfigUtil;

import java.util.HashMap;
import java.util.Map;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProfileBannerController extends SelectorComposer<Component> {

    //services
	@WireVariable BannerService bannerService;
	@WireVariable PersonService personService;
	@WireVariable AuthenticationService authService;
	@WireVariable ConfigUtil config;

	@Wire Textbox text, url;
	@Wire Label textLabel;
	@Wire Button doCreateTextAd, doCreateImageAd;

	private int maxLen;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		maxLen = config.getBannersMaxLength();
		text.setMaxlength(maxLen);
		textLabel.setValue("Текст баннера ("+maxLen+" символов)");
		doCreateTextAd.setLabel("Создать текстовый баннер(" + config.getBannerTextCost() + " iCoin)");
		doCreateImageAd.setLabel("Создать баннер с картинкой (" +config.getBannerImageCost() + " iCoin)");
	}

	@Listen("onClick = #doCreateTextAd")
    public void doCreateTextBanner() {
		showMessage(null);
    }

	@Listen("onUpload = #doCreateImageAd")
	public void doCreateImageBanner(UploadEvent event) {
		showMessage(event);
	}

	private void showMessage(final UploadEvent image) {
		final String textTrim;
		final String urlTrim = url.getValue();
		if (StringUtils.isEmpty(urlTrim)) {
			Clients.showNotification("Введите URL перехода для баннера", "warning", null, "middle_center", 2000);
			return;
		}
		if (image == null && StringUtils.isEmpty(text.getValue())) {
			Clients.showNotification("Введите текст баннера", "warning", null, "middle_center", 2000);
			return;
		}
		if (text.getValue().length() > maxLen) {
			textTrim = text.getValue().substring(0, maxLen);
		} else {
			textTrim = text.getValue();
		}

		Person p = authService.getUserCredential().getPerson();
		p = personService.findById(p.getId());
		int cost = image != null ? config.getBannerImageCost() : config.getBannerTextCost();
		if (p.getCash() < cost) {
			Clients.showNotification("Недостаточно средств", "warning", null, "middle_center", 2000, true);
			return;
		}

		Map<String, String> params = new HashMap<>();
		params.put("width", "600");
		Messagebox.show("Создать баннер за " + cost + " iCoin?",
				"Подтверждение создания баннера",
				new Messagebox.Button[] {Messagebox.Button.YES, Messagebox.Button.CANCEL},
				new String[] {"Да", "Отмена"},
				Messagebox.QUESTION,
				Messagebox.Button.OK,
				new EventListener<Messagebox.ClickEvent>() {
					@Override
					public void onEvent(Messagebox.ClickEvent event) throws Exception {
						if (event.getName().equals(Messagebox.ON_YES)) {
							if (image == null) {
								bannerService.addBanner(textTrim, urlTrim);
							} else {
								bannerService.addBanner(textTrim, urlTrim, image.getMedia().getByteData());
							}
							Clients.showNotification("Баннер успешно добавлен", "info", null, "middle_center", -1, true);
						}
					}
				}, params);
	}
}
