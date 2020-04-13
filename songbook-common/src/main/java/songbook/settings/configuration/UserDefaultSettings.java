package songbook.settings.configuration;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDefaultSettings {

    public Map<String, String> getUserDefaultSettings() {
        Map<String, String> container = new HashMap<>();

        container.put("sharing.mail.from", "roman.denisov@gmail.com");
        container.put("sharing.mail.contentMailDefaultSubject", "The Program");
        container.put("sharing.mail.contentMailBodyTemplate", "%fileList%\n\nSent using SongBook App v.2\n");
        container.put("sharing.mail.contentMailDefaultRecipients", "roman.denisov@gmail.com");

        container.put("content.gdrive.rootFolderName", "songbook");

        return container;
    }



}
