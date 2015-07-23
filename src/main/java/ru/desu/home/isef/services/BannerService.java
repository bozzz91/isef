package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.Banner;

public interface BannerService {

	void addBanner(String text, String url);

	void addBanner(String text, String url, byte[] image);

	Banner getTextBanner(Long lastId);

	Banner getImageBanner(Long lastId);
}
