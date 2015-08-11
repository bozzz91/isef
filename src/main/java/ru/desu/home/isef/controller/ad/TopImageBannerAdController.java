package ru.desu.home.isef.controller.ad;

import lombok.extern.java.Log;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.A;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import ru.desu.home.isef.entity.Banner;
import ru.desu.home.isef.services.BannerService;

import java.io.IOException;
import java.util.List;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TopImageBannerAdController extends SelectorComposer<Component> {

	//components
	@Wire Hbox bannerBox;

	private int offset = 0, activeSize = 5;

	//services
	protected @WireVariable	BannerService bannerService;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		execTimer();
	}

	@Listen("onTimer = #timer")
	public void execTimer() throws IOException {
		List<Banner> ads = bannerService.getImageBanners();
		if (ads == null || ads.isEmpty()) {
			cleanBanners(0);
			return;
		}
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
			index++;
			if (index > activeSize) {
				break;
			}

			Image image = (Image)bannerBox.getFellow("image"+index);
			image.setContent(new AImage("name", ad.getImage()));
			A url = (A) image.getParent();
			url.setHref(adUrl);
			url.setTarget("_blank");
		}
		cleanBanners(index);
	}

	private void cleanBanners(final int startIdx) {
		for (Component comp : bannerBox.getFellows()) {
			for (int idx = startIdx+1; idx<=activeSize; idx++) {
				if (comp.getId().equals("image" + idx)) {
					((Image) comp).setSrc("/imgs/banner/200x80.gif");
					comp.removeAttribute("url");
				}
			}
		}
	}
}
