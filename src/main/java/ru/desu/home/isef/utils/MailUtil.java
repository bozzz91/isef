package ru.desu.home.isef.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

public class MailUtil {

    private MailUtil() {}

    public static void send(String recipientEmail, String message, boolean productionMode) throws MessagingException {
        send(recipientEmail, "", ConfigUtil.ADMIN_EMAIL_TITLE, message, productionMode);
    }

    private static void send(String recipientEmail, String ccEmail, String title, String message, boolean productionMode) throws MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        Properties props = new Properties(System.getProperties());
        props.setProperty("mail.smtps.host", "localhost");
		props.setProperty("mail.debug", String.valueOf(!productionMode));

        Session session = Session.getInstance(props);

        final MimeMessage msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress("admin@isef.pro"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        if (ccEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        }

        msg.setSubject(title);
        msg.setText(message, "utf-8", "html");
        msg.setSentDate(new Date());

        Transport t = session.getTransport("smtps");
        try {
            t.connect();
            t.sendMessage(msg, msg.getAllRecipients());
        } finally {
            t.close();
        }
    }
}
