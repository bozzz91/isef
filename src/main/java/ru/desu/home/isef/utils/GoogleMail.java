package ru.desu.home.isef.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

public class GoogleMail {

    private GoogleMail() {
    }

    public static void send(String recipientEmail, String message) throws MessagingException {
        send(ConfigUtil.ADMIN_EMAIL, ConfigUtil.ADMIN_PASS, recipientEmail, "", ConfigUtil.ADMIN_EMAIL_TITLE, message);
    }
    
    private static void send(final String username, final String password, String recipientEmail, String title, String message) throws MessagingException {
        send(username, password, recipientEmail, "", title, message);
    }

    private static void send(final String username, final String password, String recipientEmail, String ccEmail, String title, String message) throws MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        Properties props = new Properties(System.getProperties());
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", "true");
        props.setProperty("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        final MimeMessage msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(username));
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
