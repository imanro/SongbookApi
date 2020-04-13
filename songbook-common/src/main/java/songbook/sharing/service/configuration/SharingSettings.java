package songbook.sharing.service.configuration;

import songbook.settings.service.Settings;
import songbook.settings.service.SettingsException;
import songbook.user.entity.User;

public class SharingSettings {

    private String mailFrom;

    private String mailDefaultSubject;

    private String mailBodyTemplate;

    private String mailDefaultRecipients;

    Settings settings;

    public SharingSettings(User user, Settings settings) {
        // this way because i dont want to define beans now..
        this.settings = settings;
        initSharingSettings(user);
    }

    public void initSharingSettings(User user) {
        try {
            mailFrom = settings.getValue("sharing.mail.from", user);

            mailDefaultSubject = settings.getValue("sharing.mail.contentMailDefaultSubject", user);

            mailBodyTemplate = settings.getValue("sharing.mail.contentMailBodyTemplate", user);

            mailDefaultRecipients = settings.getValue("sharing.mail.contentMailDefaultRecipients", user);

        } catch(SettingsException e) {
            throw new RuntimeException("Unable to init settings for user", e);
        }
    }

    public String getMailFrom() throws SettingsException {
        if(mailFrom == null) {
            throw new SettingsException("The settings hasn't been initialized yet");
        }

        return mailFrom;
    }

    public String getMailDefaultSubject() throws SettingsException {
        if(mailDefaultSubject == null) {
            throw new SettingsException("The settings hasn't been initialized yet");
        }

        return mailDefaultSubject;
    }

    public String getMailBodyTemplate() throws SettingsException {
        if(mailBodyTemplate == null) {
            throw new SettingsException("The settings hasn't been initialized yet");
        }

        return mailBodyTemplate;
    }

    public String getMailDefaultRecipients() throws SettingsException {
        if(mailDefaultRecipients == null) {
            throw new SettingsException("The settings hasn't been initialized yet");
        }

        return mailDefaultRecipients;
    }
}
