package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Banner;
import ru.desu.home.isef.repo.BannerRepo;
import ru.desu.home.isef.services.BannerService;
import ru.desu.home.isef.utils.ConfigUtil;

import java.util.*;

@Transactional
@Service("bannerService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BannerServiceImpl implements BannerService {

	private List<Banner> allTextBanners = new ArrayList<>();

	@Autowired BannerRepo bannerRepo;
	@Autowired ConfigUtil config;

	@Override
	public void addBanner(String text, String url) {
		addBanner(text, url, null);
	}

	@Override
	public void addBanner(String text, String url, byte[] image) {
		Banner banner = new Banner();
		banner.setText(text);
		banner.setUrl(url);
		banner.setImage(image);
		bannerRepo.save(banner);
	}

	@Override
	public List<Banner> getTextBanners() {
		int threshold = config.getTextBannersThreshold();
		int count = config.getTextBannersMaxCount();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -threshold);

		List<Banner> banners = bannerRepo.findByImageIsNullAndCreatedGreaterThanOrderByIdAsc(cal.getTime());
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

	@Override
	public List<Banner> getImageBanners() {
		int threshold = config.getImageBannersThreshold();
		int count = config.getImageBannersMaxCount();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -threshold);

		List<Banner> banners = bannerRepo.findByImageIsNotNullAndCreatedGreaterThanOrderByIdAsc(cal.getTime());
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
