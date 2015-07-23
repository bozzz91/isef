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

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class TopBannerAdController extends SelectorComposer<Component> {
    
    //components
    @Wire A url;
    
    //services
    protected @WireVariable BannerService bannerService;

    @Listen("onTimer = #timer")
    public void execTimer() {
        Banner ad = bannerService.getTextBanner();
        if (ad == null) {
            return;
        }
        url.setLabel(ad.getText());
        String adUrl = ad.getUrl();
        if (!adUrl.startsWith("http://") || !adUrl.startsWith("https://")) {
            adUrl = "http://" + adUrl;
        }

        String href = "javascript: window.open('" + adUrl + "')";
        url.setHref(href);
    }
}
