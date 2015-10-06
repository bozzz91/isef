package ru.desu.home.isef.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.Executions;
import ru.desu.home.isef.entity.Banner;
import ru.desu.home.isef.entity.Config;
import ru.desu.home.isef.repo.ConfigRepo;

import java.util.ArrayList;
import java.util.List;

@Component("config")
public class ConfigUtil {

	@Autowired ConfigRepo configRepo;

	private static final String CODE_TEXT_BANNER_PRICE = "TEXT_BANNER_PRICE";
	private static final String CODE_IMAGE_BANNER_PRICE = "IMAGE_BANNER_PRICE";
	private static final String CODE_MARQUEE_BANNER_PRICE = "MARQUEE_BANNER_PRICE";
	private static final String CODE_BECOME_REF_DIFF = "BECOME_REF_DIFF";
	private static final String CODE_BECOME_REF_INIT_PRICE = "BECOME_REF_INIT_PRICE";
	private static final String CODE_BECOME_REF_LIFE_TIME = "BECOME_REF_LIFE_TIME";
	private static final String CODE_VIP_TASK_PRICE = "VIP_TASK_PRICE";
	private static final String CODE_UNIQUE_IP_PRICE = "UNIQUE_IP_PRICE";
	private static final String CODE_PRODUCTION_MODE = "PRODUCTION_MODE";
	private static final String CODE_SAVE_ORIGIN_PASSWORD = "SAVE_ORIGIN_PASSWORD";
	private static final String CODE_ICOIN_PRICE = "ICOIN_PRICE";
	private static final String CODE_IMAGE_BANNER_MAX_ACTIVE_COUNT = "IMAGE_BANNER_MAX_ACTIVE_COUNT";
	private static final String CODE_IMAGE_BANNER_LIFE_TIME = "IMAGE_BANNER_LIFE_TIME";
	private static final String CODE_MARQUEE_BANNER_MAX_ACTIVE_COUNT = "MARQUEE_BANNER_MAX_ACTIVE_COUNT";
	private static final String CODE_MARQUEE_BANNER_LIFE_TIME = "MARQUEE_BANNER_LIFE_TIME";
	private static final String CODE_TEXT_BANNER_MAX_ACTIVE_COUNT = "TEXT_BANNER_MAX_ACTIVE_COUNT";
	private static final String CODE_TEXT_BANNER_LIFE_TIME = "TEXT_BANNER_LIFE_TIME";
	private static final String CODE_BANNER_MAX_LENGTH = "BANNER_MAX_LENGTH";
	private static final String CODE_POKUPO_IPS = "POKUPO_IPS";
	private static final String CODE_MAIL_HOST = "MAIL_HOST";
	private static final String CODE_MAIL_PORT = "MAIL_PORT";
	private static final String CODE_MAIL_TITLE = "MAIL_TITLE";
	private static final String CODE_ADDITIONAL_QUESTION_COST = "ADDITIONAL_QUESTION_COST";
	private static final String CODE_ISEF_MINIMUM_REPAY = "ISEF_MINIMUM_REPAY";
	private static final String CODE_ISEF_MINIMUM_REPAY_DAYS = "ISEF_MINIMUM_REPAY_DAYS";

	public static String getIp() {
		String ip = Executions.getCurrent().getHeader("X-Forwarded-For");
		if (ip == null) {
			ip = Executions.getCurrent().getRemoteAddr();
		}
		return ip;
	}

	public Integer getPeriod(String s) {
		String val = configRepo.findByGroupIdAndNameOrderByOrderNumberAsc(1, s).get(0).getValue().toLowerCase();
		return convertStringToMinutes(val);
	}

	public Integer getUniqueIp(String s) {
		return Integer.parseInt(configRepo.findByGroupIdAndNameOrderByOrderNumberAsc(2, s).get(0).getValue());
	}

	public Integer getShowTo(String s) {
		return Integer.parseInt(configRepo.findByGroupIdAndNameOrderByOrderNumberAsc(3, s).get(0).getValue());
	}

	public String getSex(String s) {
		return configRepo.findByGroupIdAndNameOrderByOrderNumberAsc(4, s).get(0).getValue();
	}

	public List<String> getAllPeriods() {
		List<Config> configs = configRepo.findByGroupIdOrderByOrderNumberAsc(1);
		List<String> values = new ArrayList<>();
		for (Config c : configs) {
			values.add(c.getName());
		}
		return values;
	}

	public List<String> getAllIps() {
		List<Config> configs = configRepo.findByGroupIdOrderByOrderNumberAsc(2);
		List<String> values = new ArrayList<>();
		for (Config c : configs) {
			values.add(c.getName());
		}
		return values;
	}

	public List<String> getAllReferals() {
		List<Config> configs = configRepo.findByGroupIdOrderByOrderNumberAsc(3);
		List<String> values = new ArrayList<>();
		for (Config c : configs) {
			values.add(c.getName());
		}
		return values;
	}

	public List<String> getAllSex() {
		List<Config> configs = configRepo.findByGroupIdOrderByOrderNumberAsc(4);
		List<String> values = new ArrayList<>();
		for (Config c : configs) {
			values.add(c.getName());
		}
		return values;
	}

	public String getFirstPeriod() {
		return getAllPeriods().iterator().next();
	}

	public String getFirstIp() {
		return getAllIps().iterator().next();
	}

	public String getFirstReferal() {
		return getAllReferals().iterator().next();
	}

	public String getFirstSex() {
		return getAllSex().iterator().next();
	}

	public Integer getBannerTextCost() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_TEXT_BANNER_PRICE).get(0).getValue());
	}

	public Integer getBannerImageCost() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_IMAGE_BANNER_PRICE).get(0).getValue());
	}

	public Integer getBannerMarqueeCost() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_MARQUEE_BANNER_PRICE).get(0).getValue());
	}

	public Integer getBecomeRefCost() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_BECOME_REF_INIT_PRICE).get(0).getValue());
	}

	public Integer getBecomeRefCostDiff() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_BECOME_REF_DIFF).get(0).getValue());
	}

	public Double getVipCost() {
		return Double.parseDouble(configRepo.findByCodeOrderByOrderNumberAsc(CODE_VIP_TASK_PRICE).get(0).getValue());
	}

	public Double getUniqueIpCost() {
		return Double.parseDouble(configRepo.findByCodeOrderByOrderNumberAsc(CODE_UNIQUE_IP_PRICE).get(0).getValue());
	}

	public Boolean isProduction() {
		return Boolean.parseBoolean(configRepo.findByCodeOrderByOrderNumberAsc(CODE_PRODUCTION_MODE).get(0).getValue());
	}

	public Boolean isSaveOriginPassword() {
		List<Config> options = configRepo.findByCodeOrderByOrderNumberAsc(CODE_SAVE_ORIGIN_PASSWORD);
		return !options.isEmpty() && Boolean.parseBoolean(options.get(0).getValue());
	}

	public Double getCurrency() {
		return Double.parseDouble(configRepo.findByCodeOrderByOrderNumberAsc(CODE_ICOIN_PRICE).get(0).getValue());
	}

	private Integer getImageBannersMaxCount() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_IMAGE_BANNER_MAX_ACTIVE_COUNT).get(0).getValue());
	}

	private Integer getImageBannersThreshold() {
		String val = configRepo.findByCodeOrderByOrderNumberAsc(CODE_IMAGE_BANNER_LIFE_TIME).get(0).getValue().toLowerCase();
		return convertStringToMinutes(val);
	}

	private Integer getMarqueeBannersMaxCount() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_MARQUEE_BANNER_MAX_ACTIVE_COUNT).get(0).getValue());
	}

	private Integer getMarqueeBannersThreshold() {
		String val = configRepo.findByCodeOrderByOrderNumberAsc(CODE_MARQUEE_BANNER_LIFE_TIME).get(0).getValue().toLowerCase();
		return convertStringToMinutes(val);
	}

	private Integer getTextBannersMaxCount() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_TEXT_BANNER_MAX_ACTIVE_COUNT).get(0).getValue());
	}

	private Integer getTextBannersThreshold() {
		String val = configRepo.findByCodeOrderByOrderNumberAsc(CODE_TEXT_BANNER_LIFE_TIME).get(0).getValue().toLowerCase();
		return convertStringToMinutes(val);
	}

	public Integer getBannersMaxCount(Banner.Type type) {
		switch (type) {
			case TEXT:    return getTextBannersMaxCount();
			case IMAGE:   return getImageBannersMaxCount();
			case MARQUEE: return getMarqueeBannersMaxCount();
		}
		return 0;
	}

	public Integer getBannersThreshold(Banner.Type type) {
		switch (type) {
			case TEXT:    return getTextBannersThreshold();
			case IMAGE:   return getImageBannersThreshold();
			case MARQUEE: return getMarqueeBannersThreshold();
		}
		return 0;
	}

	private Integer convertStringToMinutes(String val) {
		int mul = 1;
		if (val.contains("m")) {
			val = val.split("m")[0];
		} else if (val.contains("h")) {
			val = val.split("h")[0];
			mul = 60;
		} else if (val.contains("d")) {
			val = val.split("d")[0];
			mul = 3600;
		}
		return Integer.parseInt(val) * mul;
	}

	public Integer getBannersMaxLength() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_BANNER_MAX_LENGTH).get(0).getValue());
	}

	public Integer getBecomeRefLifeTime() {
		String val = configRepo.findByCodeOrderByOrderNumberAsc(CODE_BECOME_REF_LIFE_TIME).get(0).getValue().toLowerCase();
		return convertStringToMinutes(val);
	}

	public String[] getPokupoIps() {
		return configRepo.findByCodeOrderByOrderNumberAsc(CODE_POKUPO_IPS).get(0).getValue().toLowerCase().split(";");
	}

	public String getMailHost() {
		return configRepo.findByCodeOrderByOrderNumberAsc(CODE_MAIL_HOST).get(0).getValue().toLowerCase();
	}

	public String getMailPort() {
		return configRepo.findByCodeOrderByOrderNumberAsc(CODE_MAIL_PORT).get(0).getValue().toLowerCase();
	}

	public Double getAdditionalQuestionCost() {
		return Double.parseDouble(configRepo.findByCodeOrderByOrderNumberAsc(CODE_ADDITIONAL_QUESTION_COST).get(0).getValue());
	}

	public String getEmailTitle() {
		return configRepo.findByCodeOrderByOrderNumberAsc(CODE_MAIL_TITLE).get(0).getValue();
	}

	public Integer getMinimumRepay() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_ISEF_MINIMUM_REPAY).get(0).getValue());
	}

	public Integer getRepayInterval() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_ISEF_MINIMUM_REPAY_DAYS).get(0).getValue());
	}
}
