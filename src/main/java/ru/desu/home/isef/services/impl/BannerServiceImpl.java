package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Banner;
import ru.desu.home.isef.entity.Person;
import ru.desu.home.isef.repo.BannerRepo;
import ru.desu.home.isef.services.BannerService;
import ru.desu.home.isef.services.PersonService;
import ru.desu.home.isef.utils.ConfigUtil;

import java.util.*;

@Transactional
@Service("bannerService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BannerServiceImpl implements BannerService {

	@Autowired BannerRepo bannerRepo;
	@Autowired PersonService personService;
	@Autowired ConfigUtil config;

	@Override
	public void addBanner(Person p, double cost, String text, String url, Banner.Type type, byte[] image) {
		Banner banner = new Banner();
		banner.setText(text);
		banner.setUrl(url);
		banner.setType(type);
		banner.setImage(image);
		bannerRepo.save(banner);
		p.addCash(-cost);
		personService.save(p);
	}

	@Override
	public List<Banner> getBanners(Banner.Type type) {
		int threshold = config.getBannersThreshold(type);
		int count = config.getBannersMaxCount(type);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -threshold);

		List<Banner> banners = bannerRepo.findByTypeAndCreatedGreaterThanOrderByIdAsc(type, cal.getTime());
		if (banners != null && !banners.isEmpty()) {
			int size = banners.size();

			if (size > count) {
				int offset = banners.size() - count;
				banners = banners.subList(offset, banners.size());
			}
			return banners;
		}
		return new ArrayList<>();
	}
}
