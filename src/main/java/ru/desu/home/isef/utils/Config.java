package ru.desu.home.isef.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.springframework.util.StringUtils;
import ru.desu.home.isef.controller.LoginController;

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
}
