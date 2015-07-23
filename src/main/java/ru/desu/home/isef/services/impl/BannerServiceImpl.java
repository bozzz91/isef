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
		return getBanner(false, lastId);
	}

	@Override
	public Banner getImageBanner(Long lastId) {
		return getBanner(true, lastId);
	}

	private Banner getBanner(boolean image, Long lastId) {
		List<Banner> list = image ? allImageBanners : allTextBanners;

		Set<Long> ids = new HashSet<>();
		for (Banner banner : list) {
			ids.add(banner.getId());
		}
		ids.add(-1l);
		List<Banner> banner = image ?
				bannerRepo.findFirstByImageIsNotNullAndIdNotInOrderByIdAsc(ids) :
				bannerRepo.findFirstByImageIsNullAndIdNotInOrderByIdAsc(ids);
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

		if (size > 25) {
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
}
