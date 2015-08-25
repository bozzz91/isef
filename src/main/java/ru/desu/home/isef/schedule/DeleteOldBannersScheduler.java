package ru.desu.home.isef.schedule;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.desu.home.isef.entity.Banner;
import ru.desu.home.isef.repo.BannerRepo;
import ru.desu.home.isef.utils.ConfigUtil;

import java.util.Calendar;

@Log
@Component
@Transactional
public class DeleteOldBannersScheduler {

	@Autowired BannerRepo bannerRepo;
	@Autowired ConfigUtil config;

	@Scheduled(fixedRate = 300000)
	public void deleteBanners() {
		int threshold = config.getBannersThreshold(Banner.Type.IMAGE);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -threshold);
		int removed = bannerRepo.deleteBannersWhereCreatedLessThan(cal.getTime(), Banner.Type.IMAGE);
		if (removed > 0) {
			log.info("Removed " + removed + " old image banners (older than "+threshold+" minutes).");
		}

		threshold = config.getBannersThreshold(Banner.Type.TEXT);
		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -threshold);
		removed = bannerRepo.deleteBannersWhereCreatedLessThan(cal.getTime(), Banner.Type.TEXT);
		if (removed > 0) {
			log.info("Removed " + removed + " old text banners (older than "+threshold+" minutes).");
		}

		threshold = config.getBannersThreshold(Banner.Type.MARQUEE);
		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -threshold);
		removed = bannerRepo.deleteBannersWhereCreatedLessThan(cal.getTime(), Banner.Type.MARQUEE);
		if (removed > 0) {
			log.info("Removed " + removed + " old marquee banners (older than "+threshold+" minutes).");
		}
	}
}
