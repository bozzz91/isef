package ru.desu.home.isef.services;

import lombok.Getter;
import lombok.Setter;

public interface TextAdService {

    @Getter @Setter
    class TextAd {
        String text;
        String url;

        public TextAd(String a, String b) {
            text = a;
            url = b;
        }
    }

    void addTextAd(String text, String url);

    TextAd getTextAd();
}
