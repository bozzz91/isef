package ru.desu.home.isef.controller;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.services.BannerService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.services.auth.AuthenticationService;
import ru.desu.home.isef.utils.Config;

import java.util.HashMap;
import java.util.Map;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProfileBannerController extends SelectorComposer<Component> {

    //services
	@WireVariable BannerService bannerService;
	@WireVariable PersonService personService;
	@WireVariable AuthenticationService authService;

	@Wire Textbox text;
	@Wire Textbox url;

    @Listen("onClick = #doCreateTextAd")
    public void doCreateTextBanner() {
		showMessage(null);
    }

	@Listen("onUpload = #doCreateImageAd")
	public void doCreateImageBanner(UploadEvent event) {
		showMessage(event);
	}

	private void showMessage(final UploadEvent image) {
		Person p = authService.getUserCredential().getPerson();
		p = personService.findById(p.getId());
		int cost = image != null ? Config.BANNER_IMAGE_COST : Config.BANNER_TEXT_COST;
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
								bannerService.addBanner(text.getValue(), url.getValue());
							} else {
								bannerService.addBanner(text.getValue(), url.getValue(), image.getMedia().getByteData());
							}
							Clients.showNotification("Баннер успешно добавлен", "info", null, "middle_center", 2000);
						}
					}
				}, params);
	}
}