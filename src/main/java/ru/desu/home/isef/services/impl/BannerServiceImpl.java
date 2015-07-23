package ru.desu.home.isef.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Banner;
import ru.desu.home.isef.repo.BannerRepo;
import ru.desu.home.isef.services.BannerService;

import java.util.*;

@Service("bannerService")
@Transactional
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BannerServiceImpl implements BannerService {

	private List<Banner> allTextBanners = new ArrayList<>();
	private List<Banner> allImageBanners = new ArrayList<>();

	@Autowired BannerRepo bannerRepo;

	@Override
	public void addBanner(String text, String url) {
		Banner banner = new Banner();
		banner.setText(text);
		banner.setUrl(url);
		bannerRepo.save(banner);
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
	public Banner getTextBanner() {
		Set<Long> ids = new HashSet<>();
		for (Banner banner : allTextBanners) {
			ids.add(banner.getId());
		}
		ids.add(-1l);
		Banner banner = bannerRepo.findFirstByImageIsNullAndIdNotIn(ids);
		if (banner != null) {
			allTextBanners.add(banner);
		}
		int size = allTextBanners.size();

		if (size > 25) {
			Banner deleted = allTextBanners.remove(0);
			deleted = bannerRepo.findOne(deleted.getId());
			bannerRepo.delete(deleted);
		}

		if (size > 0) {
			int index = new Random().nextInt(size);
			Banner ad = allTextBanners.get(index);

			return ad;
		}
		return null;
	}

	@Override
	public Banner getImageBanner() {
		Set<Long> ids = new HashSet<>();
		for (Banner banner : allImageBanners) {
			ids.add(banner.getId());
		}
		ids.add(-1l);
		Banner banner = bannerRepo.findFirstByImageIsNotNullAndIdNotIn(ids);
		if (banner != null) {
			allImageBanners.add(banner);
		}
		int size = allImageBanners.size();

		if (size > 25) {
			Banner deleted = allImageBanners.remove(0);
			deleted = bannerRepo.findOne(deleted.getId());
			bannerRepo.delete(deleted);
		}

		if (size > 0) {
			int index = new Random().nextInt(size);
			Banner ad = allImageBanners.get(index);

			return ad;
		}
		return null;
	}
}
