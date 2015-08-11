package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.Banner;

import java.util.List;

public interface BannerService {

	void addBanner(String text, String url);

	void addBanner(String text, String url, byte[] image);

	Banner getTextBanner(Long lastId);

	List<Banner> getImageBanners();
}
