package ru.desu.home.isef.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DecodeUtil {

    public static String decodePass(String pass) {
        return asHex(pass, "pass");
    }
    
    public static String decodeEmail(String email) {
        return asHex(email, "mail");
    }
    
    public static String decodeReferal(String ref) {
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

        StringBuilder strBuf = new StringBuilder(buf.length * 2);

		for (byte aBuf : buf) {
			if (((int) aBuf & 0xff) < 0x10) {
				strBuf.append("0");
			}
			strBuf.append(Long.toString((int) aBuf & 0xff, 16));
		}
        return strBuf.toString();
    }
}
