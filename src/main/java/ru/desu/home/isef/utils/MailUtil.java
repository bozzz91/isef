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
import java.util.Map;
import java.util.Properties;

@Component("mail")
public class MailUtil {

	@Autowired ConfigUtil config;

	public enum MailType {
		REGISTRATION,
		RESTORE
	}

    private MailUtil() {}

    public void send(String recipientEmail, MailType type, Map<String, String> params) throws MessagingException {
		String content = generateRegistrationMail(type, params);
		String subject = generateRegistrationMail(type);

        send(recipientEmail, "", subject, content);
    }

    private void send(String recipientEmail, String ccEmail, String subject, String content) throws MessagingException {
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

        msg.setSentDate(new Date());
		msg.setSubject(subject, "utf-8");
		msg.setText(content, "utf-8", "html");

        Transport t = session.getTransport("smtp");
        try {
            t.connect();
            t.sendMessage(msg, msg.getAllRecipients());
        } finally {
            t.close();
        }
    }

	private String generateRegistrationMail(MailType type) {
		switch (type) {
			case REGISTRATION:
				return "Регистрация в ISef";
			case RESTORE:
				return "Восстановление пароля в ISef";
		}
		return config.getEmailTitle();
	}

	private String generateRegistrationMail(MailType type, Map<String, String> params) {
		switch (type) {
			case REGISTRATION:
				return "Hello " + params.get("nick") +
						"!\nYour activation code is: " + params.get("code") +
						"\nYour activation link: <a href=\"http://" + params.get("server") +
						"/activation.zul?code=" + params.get("code") + "&id=" + params.get("id") +
						"\"> Click Here</a>";
			case RESTORE:
				return "New Pass: " + params.get("pass");
		}
		throw new RuntimeException("Wrong e-mail type: " + type);
	}
}
