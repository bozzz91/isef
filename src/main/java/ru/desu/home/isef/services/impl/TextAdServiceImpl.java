package ru.desu.home.isef.services.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.services.TextAdService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service("textAdService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TextAdServiceImpl implements TextAdService {

    private List<TextAd> allAds = new ArrayList<>();

    @Override
    public void addTextAd(String text, String url) {
        allAds.add(new TextAd(text, url));
    }

    @Override
    public TextAd getTextAd() {
        int size = allAds.size();

        if (size > 0) {
            int index = new Random().nextInt(size);
            TextAd ad = allAds.get(index);

            return ad;
        }
        return null;
    }
}
