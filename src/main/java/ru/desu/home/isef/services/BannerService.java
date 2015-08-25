package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.Banner;

import java.util.List;

public interface BannerService {

	void addBanner(String text, String url, Banner.Type type, byte[] image);

	List<Banner> getBanners(Banner.Type type);
}
