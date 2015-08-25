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
public class TopMarqueeBannerAdController extends SelectorComposer<Component> {

    //components
    @Wire A marquee;

	private int offset = 0;

	//services
	protected @WireVariable	BannerService bannerService;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		execTimer();
	}

	@Listen("onTimer = #timer1")
	public void execTimer() throws IOException {
		List<Banner> ads = bannerService.getBanners(Banner.Type.MARQUEE);
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
			marquee.setHref(href);
			marquee.setLabel(ad.getText());
			marquee.setVisible(true);
			marquee.setTarget("_blank");

			index++;
			if (index > activeSize) {
				break;
			}
		}
	}

	private void cleanBanner() {
		marquee.setHref(null);
		marquee.setVisible(false);
		marquee.setLabel("");
	}
}
