package ru.desu.home.isef.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.FatalBeanException;

public class PasswordUtil {

    public static String asHex(String pass, String salt) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException ex) {
            throw new FatalBeanException("md5 not found");
        }
        md.update((pass + ":" + salt).getBytes());
        byte[] buf = md.digest();

        StringBuilder strbuf = new StringBuilder(buf.length * 2);

        for (int i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }
}
