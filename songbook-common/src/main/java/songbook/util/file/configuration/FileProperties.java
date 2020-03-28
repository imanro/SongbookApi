package songbook.util.file.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// @see https://www.petrikainulainen.net/programming/spring-framework/spring-from-the-trenches-injecting-property-values-into-configuration-beans/
// https://www.baeldung.com/spring-profiles
// we load the value from songbook-rest (sic) /application-default.properties so far, because songbook-rest is the host app for songbook-common
// spring profile can be set through environment (see tomcat host config)
// if the .properties file is placed along with war, it should be parsed


@Component
public class FileProperties {

    private String tmpDirFsPath;

    @Autowired // checkme is it needed
    public FileProperties(
            @Value("${songbook.util.file.tmpDirFsPath}") String tmpDirFsPath
    ) {
        this.tmpDirFsPath = tmpDirFsPath;
    }

    public String getTmpDirFsPath() throws Exception {

        if (tmpDirFsPath == null) {
            throw new Exception("tmpDirFsPath is not set by properties");
        }

        return tmpDirFsPath;
    }
}
