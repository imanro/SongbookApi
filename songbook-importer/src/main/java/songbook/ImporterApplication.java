package songbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.*;
import songbook.importer.service.ContentImporter;
import songbook.importer.service.SongImporter;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;


@SpringBootApplication(scanBasePackages = "songbook")
@Configuration
@EnableAutoConfiguration
public class ImporterApplication implements ApplicationRunner {

    @Autowired
    private SongImporter songImporter;

    @Autowired
    private ContentImporter contentImporter;

    private static final String ARG_MODE = "m";

    private static final String MODE_SONG = "song";

    private static final String MODE_CONTENT = "content";

    public static void main(String... args) {
        // This way we just start a command line application
        SpringApplication app = new SpringApplication(ImporterApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Override
    public void run(ApplicationArguments args) {

        System.out.println("Hello, Im Importer");

        Properties prop;

        try {
            prop = loadPropertiesFile();
        } catch (IOException e) {
            System.out.println("Unable to load property file");
        }

        List<String> values = args.getOptionValues(ARG_MODE);
        String mode;

        if (values != null) {
            mode = values.get(0);
            if (mode != null) {
                switch (mode) {
                    case(MODE_SONG):
                        System.out.println("Running Song Import...");
                        songImporter.runImport();
                        break;
                    case(MODE_CONTENT):
                        System.out.println("Running Content Import...");
                        contentImporter.runImport();
                        break;
                    default:
                        System.err.println("Unknown mode given, exiting");
                        break;
                }
            } else {
                System.err.println("Unknown mode given, exiting");
            }

        } else {
            System.err.println("Unknown mode given, exiting");
        }
    }

    // Not used so far
    private Properties loadPropertiesFile() throws IOException {

        String filename = "config.properties";
        try (InputStream input = ImporterApplication.class.getClassLoader().getResourceAsStream(filename)) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                throw new RuntimeException("Unable to load property file");
            }

            prop.load(input);
            return prop;

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
