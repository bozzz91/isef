package ru.desu.home.isef.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DecodeUtil {

    public static final String decodePass(String pass) {
        return asHex(pass, "pass");
    }
    
    public static final String decodeEmail(String email) {
        return asHex(email, "mail");
    }
    
    public static final String decodeReferal(String ref) {
        return asHex(ref, "referal");
    }
    
    private static String asHex(String pass, String salt) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("md5 not found", ex);
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
