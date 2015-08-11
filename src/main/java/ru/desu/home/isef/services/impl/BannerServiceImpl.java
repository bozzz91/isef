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
	public Banner getTextBanner(Long lastId) {
		int threshold = config.getTextBannersThreshold();
		int count = config.getTextBannersMaxCount();

		List<Banner> list = allTextBanners;

		Set<Long> ids = new HashSet<>();
		for (Banner banner : list) {
			ids.add(banner.getId());
		}
		ids.add(-1l);
		List<Banner> banner = bannerRepo.findFirstByImageIsNullAndIdNotInOrderByIdAsc(ids);
		if (banner != null && !banner.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -24);
			if (!banner.get(0).getCreated().before(cal.getTime())) {
				list.add(banner.get(0));
			} else {
				bannerRepo.delete(banner.get(0));
			}
		}
		int size = list.size();

		if (size > count) {
			Banner deleted = list.remove(0);
			deleted = bannerRepo.findOne(deleted.getId());
			bannerRepo.delete(deleted);
		}

		if (size > 0) {
			int index = new Random().nextInt(size);
			Banner ad = list.get(index);
			if (!ad.getId().equals(lastId)) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.HOUR, -24);
				if (!ad.getCreated().before(cal.getTime())) {
					return ad;
				} else {
					bannerRepo.delete(ad);
					list.remove(index);
				}
			}
			return null;
		}
		return null;
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
