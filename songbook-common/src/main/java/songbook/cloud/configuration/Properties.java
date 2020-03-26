package songbook.cloud.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// @see https://www.petrikainulainen.net/programming/spring-framework/spring-from-the-trenches-injecting-property-values-into-configuration-beans/
// https://www.baeldung.com/spring-profiles
// we load the value from songbook-rest (sic) /application-default.properties so far, because songbook-rest is the host app for songbook-common
// spring profile can be set through environment (see tomcat host config)
// if the .properties file is placed along with war, it should be parsed



@Component
public class Properties {

    private String rootFolderName;

    @Autowired // checkme is it needed
    public Properties(
            @Value("${songbook.cloud.driver.gdrive.root-folder-name}") String rootFolderName
    ) {
        this.rootFolderName = rootFolderName;
    }

    public String getRootFolderName() throws RuntimeException {

        if (rootFolderName == null) {
            throw new RuntimeException("root-folder-name is not set by properties");
        }

        return rootFolderName;
    }
}
