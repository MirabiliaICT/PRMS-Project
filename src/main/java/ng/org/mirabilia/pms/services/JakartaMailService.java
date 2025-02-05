package ng.org.mirabilia.pms.services;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class JakartaMailService {

    public void sendSingle(String from, String password, String recipient, String subject, String text,
                           InputStream attachment, String fileName, String mimeType) throws MessagingException, IOException {

        // Set mail server properties
        Properties properties = new Properties();
//        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.connectiontimeout", "10000"); // 10 seconds
        properties.put("mail.smtp.timeout", "10000");          // 10 seconds
        properties.put("mail.smtp.writetimeout", "10000");     // 10 seconds


        // Create a mail session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        // Create a new email message
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject(subject);

        // Create the email body
        MimeMultipart multipart = new MimeMultipart();

        // Add the text part
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(text, "text/html; charset=utf-8");
        multipart.addBodyPart(textPart);

        // Add the attachment
        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource dataSource = new ByteArrayDataSource(attachment, mimeType);
        attachmentPart.setDataHandler(new DataHandler(dataSource));
        attachmentPart.setFileName(fileName);
        multipart.addBodyPart(attachmentPart);

        // Set the email content
        message.setContent(multipart);

        // Send the email
        Transport.send(message);
        System.out.println("Email sent successfully!");
    }
}



