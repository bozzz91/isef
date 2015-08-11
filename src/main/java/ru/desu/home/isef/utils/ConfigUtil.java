package ru.desu.home.isef.utils;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Executions;
import ru.desu.home.isef.controller.LoginController;
import ru.desu.home.isef.entity.Config;
import ru.desu.home.isef.repo.ConfigRepo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

@Log
@Component("config")
public class ConfigUtil {

	@Autowired ConfigRepo configRepo;

    private static final Properties props = new Properties();

	private static final String CODE_TEXT_BANNER_PRICE = "TEXT_BANNER_PRICE";
	private static final String CODE_IMAGE_BANNER_PRICE = "IMAGE_BANNER_PRICE";
	private static final String CODE_BECOME_REF_DIFF = "BECOME_REF_DIFF";
	private static final String CODE_BECOME_REF_INIT_PRICE = "BECOME_REF_INIT_PRICE";
	private static final String CODE_VIP_TASK_PRICE = "VIP_TASK_PRICE";
	private static final String CODE_UNIQUE_IP_PRICE = "UNIQUE_IP_PRICE";
	private static final String CODE_PRODUCTION_MODE = "PRODUCTION_MODE";
	private static final String CODE_ICOIN_PRICE = "ICOIN_PRICE";
	private static final String CODE_IMAGE_BANNER_MAX_ACTIVE_COUNT = "IMAGE_BANNER_MAX_ACTIVE_COUNT";
	private static final String CODE_IMAGE_BANNER_LIFE_TIME = "IMAGE_BANNER_LIFE_TIME";
	private static final String CODE_TEXT_BANNER_MAX_ACTIVE_COUNT = "TEXT_BANNER_MAX_ACTIVE_COUNT";
	private static final String CODE_TEXT_BANNER_LIFE_TIME = "TEXT_BANNER_LIFE_TIME";
	private static final String CODE_BANNER_MAX_LENGTH = "BANNER_MAX_LENGTH";


	public static final String ADMIN_EMAIL;
    public static final String ADMIN_PASS;
    public static final String ADMIN_EMAIL_TITLE;
    public static final String ISEF_MINIMUM_REPAY;
    public static final String ISEF_MINIMUM_REPAY_DAYS;
    public static final String ISEF_CODE;
    public static final String ONPAY_CONVERT;
    public static final String ONPAY_CURRENCY;
    public static final String ONPAY_PRICE_FINAL;

	public static final String DB_URL;
	public static final String DB_LOGIN;
	public static final String DB_PASS;
	public static final String DB_DRIVER;
    
    static {
        try {
            props.load(LoginController.class.getResourceAsStream("/config.txt"));
			String config = System.getProperty("isef.config", "../conf/isef.properties");
			props.load(new FileInputStream(config));
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        ADMIN_EMAIL = props.getProperty("admin_email");
        ADMIN_PASS = props.getProperty("admin_pass");
        ADMIN_EMAIL_TITLE = props.getProperty("admin_email_title");
        ISEF_MINIMUM_REPAY = props.getProperty("minimum_pay");
        ISEF_MINIMUM_REPAY_DAYS = props.getProperty("minimum_pay_day");
        ISEF_CODE = props.getProperty("isef_code");
        ONPAY_CONVERT = props.getProperty("convert");
        ONPAY_CURRENCY = props.getProperty("currency");
        ONPAY_PRICE_FINAL = props.getProperty("price_final");

		DB_URL = props.getProperty("db_url");
		DB_LOGIN = props.getProperty("db_login");
		DB_PASS = props.getProperty("db_pass");
		DB_DRIVER =props.getProperty("db_driver");

        ArrayList<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(ADMIN_EMAIL)) {
            errors.add("admin_email");
        }
        if (StringUtils.isEmpty(ADMIN_EMAIL_TITLE)) {
            errors.add("admin_email_title");
        }
        if (StringUtils.isEmpty(ADMIN_PASS)) {
            errors.add("admin_pass");
        }
        if (StringUtils.isEmpty(ISEF_MINIMUM_REPAY)) {
            errors.add("minimum_pay");
        }
        if (StringUtils.isEmpty(ISEF_MINIMUM_REPAY_DAYS)) {
            errors.add("minimum_pay_day");
        }
        if (StringUtils.isEmpty(ISEF_CODE)) {
            errors.add("isef_code");
        }
        if (StringUtils.isEmpty(ONPAY_CONVERT)) {
            errors.add("convert");
        }
        if (StringUtils.isEmpty(ONPAY_CURRENCY)) {
            errors.add("currency");
        }
        if (StringUtils.isEmpty(ONPAY_PRICE_FINAL)) {
            errors.add("price_final");
        }
		if (StringUtils.isEmpty(DB_DRIVER)) {
			errors.add("db_driver");
		}
		if (StringUtils.isEmpty(DB_LOGIN)) {
			errors.add("db_login");
		}
		if (StringUtils.isEmpty(DB_PASS)) {
			errors.add("db_pass");
		}
		if (StringUtils.isEmpty(DB_URL)) {
			errors.add("db_url");
		}
        if (!errors.isEmpty())
            throw new IllegalArgumentException("Неверные параметры "+Arrays.toString(errors.toArray())+" в config.txt");
        try {
            int a = Integer.parseInt(ISEF_MINIMUM_REPAY);
			log.info(ISEF_MINIMUM_REPAY + " = " + a);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный параметр 'minimum_pay' в config.txt", e);
        }
        try {
            int a = Integer.parseInt(ISEF_MINIMUM_REPAY_DAYS);
			log.info(ISEF_MINIMUM_REPAY_DAYS + " = " + a);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный параметр 'minimum_pay_day' в config.txt", e);
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Неверные параметры " + Arrays.toString(errors.toArray()) + " в config.txt");
        }
    }

	public static String getIp() {
		String ip = Executions.getCurrent().getHeader("X-Forwarded-For");
		if (ip == null) {
			ip = Executions.getCurrent().getRemoteAddr();
		}
		return ip;
	}

	public Integer getPeriod(String s) {
		return Integer.parseInt(configRepo.findByGroupIdAndNameOrderByOrderNumberAsc(1, s).get(0).getValue());
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

	public Double getCurrency() {
		return Double.parseDouble(configRepo.findByCodeOrderByOrderNumberAsc(CODE_ICOIN_PRICE).get(0).getValue());
	}

	public Integer getImageBannersMaxCount() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_IMAGE_BANNER_MAX_ACTIVE_COUNT).get(0).getValue());
	}

	public Integer getImageBannersThreshold() {
		String val = configRepo.findByCodeOrderByOrderNumberAsc(CODE_IMAGE_BANNER_LIFE_TIME).get(0).getValue().toLowerCase();
		return convertStringToMinutes(val);
	}

	public Integer getTextBannersMaxCount() {
		return Integer.parseInt(configRepo.findByCodeOrderByOrderNumberAsc(CODE_TEXT_BANNER_MAX_ACTIVE_COUNT).get(0).getValue());
	}

	public Integer getTextBannersThreshold() {
		String val = configRepo.findByCodeOrderByOrderNumberAsc(CODE_TEXT_BANNER_LIFE_TIME).get(0).getValue().toLowerCase();
		return convertStringToMinutes(val);
	}

	private static Integer convertStringToMinutes(String val) {
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
}
