//package edu.muzraev.app.service;
//
//import com.sun.mail.smtp.SMTPTransport;
//import org.springframework.stereotype.Service;
//
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.NoSuchProviderException;
//import javax.mail.Session;
//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import java.util.Date;
//import java.util.Properties;
//
//import static edu.muzraev.app.constants.EmailConstants.*;
//import static javax.mail.Message.RecipientType.TO;
//
//@Service
//public class EmailService {
//
//
//    public void sendNewPasswordEmail(String firstName, String password, String email)  {
//        Message message = null;
//        try {
//            message = createEmail(firstName, password, email);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        SMTPTransport smtpTransport = null;
//        try {
//            smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        }
//        try {
//            if (smtpTransport != null) {
//                smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
//            }
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        try {
//            if (message != null) {
//                if (smtpTransport != null) {
//                    smtpTransport.sendMessage(message, message.getAllRecipients());
//                }
//            }
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        try {
//            if (smtpTransport != null) {
//                smtpTransport.close();
//            }
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Message createEmail(String firstName, String password, String email) throws MessagingException {
//        Message message = new MimeMessage(getEmailSession());
//        message.setFrom(new InternetAddress(FROM_EMAIL));
//        message.setRecipients(TO, InternetAddress.parse(email, false));
//        message.setRecipients(TO, InternetAddress.parse(CC_EMAIL, false));
//        message.setSubject(EMAIL_SUBJECT);
//        message.setText("Hello " + firstName+ ", \n \n Your new account password is " + password + "\n \n The Support Team");
//        message.setSentDate(new Date());
//        message.saveChanges();
//        return message;
//    }
//    private Session getEmailSession() {
//        Properties properties = System.getProperties();
//        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
//        properties.put(SMTP_AUTH, true);
//        properties.put(SMTP_PORT, DEFAULT_PORT);
//        properties.put(SMTP_STARTTLS_ENABLE, true);
//        properties.put(SMTP_STARTTLS_REQUIRED, true);
//        return Session.getInstance(properties,  null);
//    }
//}