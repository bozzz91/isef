package ru.desu.home.isef.controller.ad;

import lombok.extern.java.Log;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.A;
import org.zkoss.zul.Image;
import ru.desu.home.isef.entity.Banner;
import ru.desu.home.isef.services.BannerService;

import java.io.IOException;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TopImageBannerAdController extends SelectorComposer<Component> {

	//components
	@Wire Image image;
	@Wire A text;

	private Long lastId;

	//services
	protected @WireVariable	BannerService bannerService;

	@Listen("onTimer = #timer2")
	public void execTimer() throws IOException {
		Banner ad = bannerService.getImageBanner(lastId);
		if (ad == null) {
			return;
		}
		lastId = ad.getId();
		text.setLabel(ad.getText());
		String adUrl = ad.getUrl();
		if (!adUrl.startsWith("http://") || !adUrl.startsWith("https://")) {
			adUrl = "http://" + adUrl;
		}

		String href = "javascript: window.open('" + adUrl + "')";
		image.setContent(new AImage("name", ad.getImage()));
		image.setAttribute("url", href);
		text.setHref(href);
	}

	@Listen("onClick = #image")
	public void clickImage() {
		String url = (String) image.getAttribute("url");
		Executions.getCurrent().sendRedirect(url, "_blank");
	}
}
