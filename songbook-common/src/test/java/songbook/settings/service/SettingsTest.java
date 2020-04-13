package songbook.settings.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import songbook.settings.configuration.UserDefaultSettings;
import songbook.settings.repository.SettingDao;
import songbook.user.entity.User;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SettingsTest {

    // Spy is contrary to Mock, allows to inject real objects
    @Spy
    UserDefaultSettings userDefaultSettings;

    @Mock
    SettingDao settingsDao;

    @Spy
    @InjectMocks
    private Settings settingsMock;

    @Test
    void defaultSettingsCanBeAssignedToUser() throws SettingsException {
        User user = new User();
        user.setEmail("nobody333@example.com");

        Map<String, String> defaultSettings = userDefaultSettings.getUserDefaultSettings();

        // try to get any setting
        settingsMock.getValue("Foo", user);

        // check that the class will call setter for settings
        verify(settingsMock,times(defaultSettings.size())).setValue(any(), any(), any());
    }


}
