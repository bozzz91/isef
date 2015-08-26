package ru.desu.home.isef.services;

import ru.desu.home.isef.entity.Banner;
import ru.desu.home.isef.entity.Person;

import java.util.List;

public interface BannerService {

	void addBanner(Person p, double cost, String text, String url, Banner.Type type, byte[] image);

	List<Banner> getBanners(Banner.Type type);
}
