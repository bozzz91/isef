package ru.desu.home.isef.controller.ad;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.A;
import ru.desu.home.isef.entity.Banner;
import ru.desu.home.isef.services.BannerService;

import java.io.IOException;
import java.util.List;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TopBannerAdController extends SelectorComposer<Component> {

    //components
    @Wire A url;

	private int offset = 0;

	//services
	protected @WireVariable	BannerService bannerService;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		execTimer();
	}

	@Listen("onTimer = #timer")
	public void execTimer() throws IOException {
		List<Banner> ads = bannerService.getBanners(Banner.Type.TEXT);
		if (ads == null || ads.isEmpty()) {
			cleanBanner();
			return;
		}
		int activeSize = 1;
		if (ads.size() > activeSize) {
			int fullSize = ads.size();
			ads = ads.subList(offset, offset+activeSize);
			if (++offset > fullSize - activeSize) {
				offset = 0;
			}
		} else {
			offset = 0;
		}

		int index = 0;
		for (Banner ad : ads) {
			String adUrl = ad.getUrl();
			if (!adUrl.startsWith("http://") || !adUrl.startsWith("https://")) {
				adUrl = "http://" + adUrl;
			}
			//
			String href = adUrl;
			url.setHref(href);
			url.setLabel(ad.getText());
			url.setVisible(true);
			url.setTarget("_blank");

			index++;
			if (index > activeSize) {
				break;
			}
		}
	}

	private void cleanBanner() {
		url.setHref(null);
		url.setVisible(false);
		url.setLabel("");
	}
}
