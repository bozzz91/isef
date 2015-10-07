package ru.desu.home.isef.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Component("mail")
public class MailUtil {

	@Autowired ConfigUtil config;

    private MailUtil() {}

    public void send(String recipientEmail, String message) throws MessagingException {
        send(recipientEmail, "", config.getEmailTitle(), message);
    }

    private void send(String recipientEmail, String ccEmail, String title, String message) throws MessagingException {
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

        msg.setSubject(title, "utf-8");
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
