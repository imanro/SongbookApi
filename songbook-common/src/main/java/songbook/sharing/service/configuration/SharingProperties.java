package songbook.sharing.service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// @see https://www.petrikainulainen.net/programming/spring-framework/spring-from-the-trenches-injecting-property-values-into-configuration-beans/
// https://www.baeldung.com/spring-profiles
// we load the value from songbook-rest (sic) /application-default.properties so far, because songbook-rest is the host app for songbook-common
// spring profile can be set through environment (see tomcat host config)
// if the .properties file is placed along with war, it should be parsed
@Component
public class SharingProperties {

    private String mailSmtpHost;
    private String mailSmtpPort;
    private String mailSmtpUsername;
    private String mailSmtpPassword;

    @Autowired // checkme is it needed
    public SharingProperties(
            @Value("${sharing.mail.smtp.host}") String mailSmtpHost,
            @Value("${sharing.mail.smtp.port}") String mailSmtpPort,
            @Value("${sharing.mail.auth.username}") String mailAuthUsername,
            @Value("${sharing.mail.auth.password}") String mailAuthPassword
    ) {
        this.mailSmtpHost = mailSmtpHost;
        this.mailSmtpPort = mailSmtpPort;
        this.mailSmtpUsername = mailAuthUsername;
        this.mailSmtpPassword = mailAuthPassword;
    }

    public String getMailSmtpHost() {
        if (mailSmtpHost == null) {
            throw new RuntimeException("smtpHost is not set by properties");
        }

        return mailSmtpHost;
    }

    public String getMailSmtpPort() {
        if (mailSmtpHost == null) {
            throw new RuntimeException("smtpPort is not set by properties");
        }

        return mailSmtpPort;
    }

    public String getMailSmtpUsername() {
        if (mailSmtpHost == null) {
            throw new RuntimeException("smtpUsername is not set by properties");
        }

        return mailSmtpUsername;
    }

    public String getMailSmtpPassword() {
        if (mailSmtpPassword == null) {
            throw new RuntimeException("smtpPassword is not set by properties");
        }

        return mailSmtpPassword;
    }
}
