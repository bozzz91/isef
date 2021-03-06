package ru.desu.home.isef.controller.profile;

import lombok.extern.java.Log;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Component;
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
import ru.desu.home.isef.entity.Banner;
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
	@Wire Button doCreateTextAd, doCreateImageAd, doCreateMarqueeAd;

	private int maxLen;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		maxLen = config.getBannersMaxLength();
		text.setMaxlength(maxLen);
		textLabel.setValue("Текст баннера (" + maxLen + " символов)");
		doCreateTextAd.setLabel("Создать текстовый баннер (" + config.getBannerTextCost() + " iCoin)");
		doCreateImageAd.setLabel("Создать баннер с картинкой (" +config.getBannerImageCost() + " iCoin)");
		doCreateMarqueeAd.setLabel("Создать баннер в бегущей строке (" +config.getBannerMarqueeCost() + " iCoin)");
	}

	@Listen("onClick = #doCreateTextAd")
    public void doCreateTextBanner() {
		showMessage(Banner.Type.TEXT, null);
    }

	@Listen("onUpload = #doCreateImageAd")
	public void doCreateImageBanner(UploadEvent event) {
		showMessage(Banner.Type.IMAGE, event);
	}

	@Listen("onClick = #doCreateMarqueeAd")
	public void doCreateMarqueeAd() {
		showMessage(Banner.Type.MARQUEE, null);
	}

	/**
	 * types:
	 *  0 - text banner (left zone)
	 *  1 - image banner (top)
	 *  2 - text marquee banner (top)
	 */
	private void showMessage(final Banner.Type type, final UploadEvent image) {
		final String textTrim;
		final String urlTrim = url.getValue();
		if (StringUtils.isEmpty(urlTrim)) {
			Clients.showNotification("Введите URL перехода для баннера", "warning", null, "middle_center", 2000);
			return;
		}
		if ((type == Banner.Type.TEXT || type == Banner.Type.MARQUEE) && StringUtils.isEmpty(text.getValue())) {
			Clients.showNotification("Введите текст баннера", "warning", null, "middle_center", 2000);
			return;
		}
		if (text.getValue().length() > maxLen) {
			textTrim = text.getValue().substring(0, maxLen);
		} else {
			textTrim = text.getValue();
		}

		final Person p = personService.findById(authService.getUserCredential().getPerson().getId());
		final int cost;
		switch (type) {
			case TEXT:    cost = config.getBannerTextCost();    break;
			case IMAGE:   cost = config.getBannerImageCost();   break;
			case MARQUEE: cost = config.getBannerMarqueeCost(); break;
			default:	  cost = 0;
		}
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
				event -> {
					if (event.getName().equals(Messagebox.ON_YES)) {
						bannerService.addBanner(p, cost, textTrim, urlTrim, type, image == null ? null : image.getMedia().getByteData());
						Clients.showNotification("Баннер успешно добавлен", "info", null, "middle_center", -1, true);
					}
				}, params);
	}
}
