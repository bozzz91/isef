package ru.desu.home.isef.utils;

import lombok.extern.java.Log;
import org.springframework.util.StringUtils;
import org.zkoss.zk.ui.Executions;
import ru.desu.home.isef.controller.LoginController;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

@Log
public class Config {
    private static final Properties props = new Properties();
    
    public static final String ADMIN_EMAIL;
    public static final String ADMIN_PASS;
    public static final String ADMIN_EMAIL_TITLE;
    public static final String ISEF_MINIMUM_REPAY;
    public static final String ISEF_MINIMUM_REPAY_DAYS;
    public static final String HOST_LINK;
    public static final String ISEF_CODE;
    public static final String ONPAY_CONVERT;
    public static final String ONPAY_CURRENCY;
    public static final String ONPAY_PRICE_FINAL;
    public static final boolean IS_PRODUCTION;
    
    static {
        try {
            props.load(LoginController.class.getResourceAsStream("/config.txt"));
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        ADMIN_EMAIL = props.getProperty("admin_email");
        ADMIN_PASS = props.getProperty("admin_pass");
        ADMIN_EMAIL_TITLE = props.getProperty("admin_email_title");
        HOST_LINK = props.getProperty("host_link");
        IS_PRODUCTION = Boolean.valueOf(props.getProperty("production"));
        ISEF_MINIMUM_REPAY = props.getProperty("minimum_pay");
        ISEF_MINIMUM_REPAY_DAYS = props.getProperty("minimum_pay_day");
        ISEF_CODE = props.getProperty("isef_code");
        ONPAY_CONVERT = props.getProperty("convert");
        ONPAY_CURRENCY = props.getProperty("currency");
        ONPAY_PRICE_FINAL = props.getProperty("price_final");

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
        if (StringUtils.isEmpty(HOST_LINK)) {
            errors.add("host_link");
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
        if (!errors.isEmpty())
            throw new IllegalArgumentException("Неверные параметры "+Arrays.toString(errors.toArray())+" в config.txt");
        try {
            Integer.parseInt(ISEF_MINIMUM_REPAY);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный параметр 'minimum_pay' в config.txt", e);
        }
        try {
            Integer.parseInt(ISEF_MINIMUM_REPAY_DAYS);
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

	private static HashMap<String, Integer> periods = new LinkedHashMap<>();

	static {
		periods.put("Раз в 6 часов", 6);
		periods.put("Раз в 12 часов", 12);
		periods.put("Раз в 1 день", 24);
		periods.put("Раз в 2 дня", 48);
		periods.put("Раз в 5 дней", 120);
	}

	public static Integer getPeriod(String s) {
		return periods.get(s);
	}

	public static Set<String> getAllPeriods() {
		return periods.keySet();
	}

	public static String getFirstPeriod() {
		return periods.entrySet().iterator().next().getKey();
	}
}
