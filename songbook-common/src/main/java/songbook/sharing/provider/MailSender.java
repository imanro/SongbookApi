package songbook.sharing.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.settings.service.Settings;
import songbook.settings.service.SettingsException;
import songbook.sharing.configuration.SharingProperties;
import songbook.sharing.configuration.SharingSettings;
import songbook.song.entity.SongContent;
import songbook.user.entity.User;
import songbook.util.file.entity.FileHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

@Service
public class MailSender {

    @Autowired
    private SharingProperties properties;

    @Autowired
    private Settings settingsService;

    private Session mailSession;

    public MimeMessage createContentMail(String recipients, String subject, String body, User user, List<FileHolder> files) throws MailSenderException {

        SharingSettings userSettings = new SharingSettings(user, settingsService);
        MimeMessage message = new MimeMessage(getMailSession());

        try {
            message.setFrom(new InternetAddress(userSettings.getMailFrom()));
        } catch (SettingsException e) {
            throw new MailSenderException("Unable to read user settings due to exception", e);
        } catch (AddressException e) {
            throw new MailSenderException("Wrong settings given for the \"from\" address", e);
        } catch (MessagingException e) {
            throw new MailSenderException("An exception has occurred when setting \"from\" address", e);
        }

        try {
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipients));
        } catch (AddressException e) {
            throw new MailSenderException("An exception has occurred while parsing \"to\" addresses", e);
        } catch (MessagingException e) {
            throw new MailSenderException("An exception has occurred when setting \"to\" addresses", e);
        }

        try {
            message.setSubject(subject);
        } catch (MessagingException e) {
            throw new MailSenderException("An exception has occurred when setting subject", e);
        }

        Multipart multipart = new MimeMultipart();

        try {
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(body);
            multipart.addBodyPart(textBodyPart);
        } catch(MessagingException e) {
            throw new MailSenderException("An exception has occurred when adding the message body", e);
        }

        // adding files
        addDownloadedFiles(files, multipart);

        try {
            message.setContent(multipart);
        } catch (MessagingException e) {
            throw new MailSenderException("Unable to add multipart part into the message, an exception occurred", e);
        }

        return message;
    }

    public Session getMailSession() {
        if(mailSession == null) {
            initMailSession();
        }

        return mailSession;
    }

    private void addDownloadedFiles(List<FileHolder> files, Multipart multipart) throws MailSenderException {
        // adding downloaded files
        for(FileHolder fileHolder : files ) {

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(fileHolder.getFile().getAbsolutePath());

            try {
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(fileHolder.getOriginalName());
                multipart.addBodyPart(messageBodyPart);
            } catch(MessagingException e) {
                throw new MailSenderException("An error has occurred when adding a body part", e);
            }
        }
    }

    private void initMailSession() {
        final String username = properties.getMailSmtpUsername();
        final String password = properties.getMailSmtpPassword();

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", properties.getMailSmtpHost());
        props.put("mail.smtp.port", properties.getMailSmtpPort());
        //        props.put("mail.debug", "true");
        props.put("mail.smtp.starttls.enable", "true");


        this.mailSession = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

}
