package com.app.service.service;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static final String EMAIL_ADDRESS = "@gmail.com";
    private static final String EMAIL_PASSWORD = "";

    // https://j2html.com/
    public void sendAsHtml(String to, String title, String html) {
        try {
            System.out.println("Sending email to " + to);
            Session session = createSession();

            MimeMessage mimeMessage = new MimeMessage(session);
            prepareEmailMessage(mimeMessage, to, title, html);

            Transport.send(mimeMessage);
            System.out.println("Email has been sent");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void prepareEmailMessage(MimeMessage message, String to, String title, String html) {
        try {
            message.setContent(html, "text/html; charset=utf-8");
            message.setFrom(new InternetAddress(EMAIL_ADDRESS));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(title);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private javax.mail.Session createSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        return javax.mail.Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_ADDRESS, EMAIL_PASSWORD);
            }
        });
    }
}
