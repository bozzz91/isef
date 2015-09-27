package ru.desu.home.isef.utils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class MailUtil {

    private MailUtil() {}

    public static void send(String recipientEmail, String message, ConfigUtil config) throws MessagingException {
        send(recipientEmail, "", ConfigUtil.ADMIN_EMAIL_TITLE, message, config);
    }

    private static void send(String recipientEmail, String ccEmail, String title, String message, ConfigUtil config) throws MessagingException {
        Properties props = new Properties();
		props.setProperty("mail.smtp.host", config.getMailHost());
		props.setProperty("mail.smtp.port", config.getMailPort());
		props.setProperty("mail.debug", String.valueOf(!config.isProduction()));

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

        Transport t = session.getTransport("smtp");
        try {
            t.connect();
            t.sendMessage(msg, msg.getAllRecipients());
        } finally {
            t.close();
        }
    }
}
