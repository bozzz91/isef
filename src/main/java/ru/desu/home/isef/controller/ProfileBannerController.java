package ru.desu.home.isef.controller;

import lombok.extern.java.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import ru.desu.home.isef.services.TextAdService;

import java.util.Random;

@Log
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProfileBannerController extends SelectorComposer<Component> {

    //services
    protected @WireVariable TextAdService textAdService;

    @Listen("onClick = #doCreateTextAd")
    public void doCreateTextAd() {
        textAdService.addTextAd("as" + new Random().nextInt(100), "www.yandex.ru");
    }
}
