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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SettingsServiceTest {

    // Spy is contrary to Mock, allows to inject real objects
    @Spy
    UserDefaultSettings userDefaultSettings;

    @Mock
    SettingDao settingsDao;

    @Spy
    @InjectMocks
    private SettingsService settingsService;

    @Test
    void defaultSettingsCanBeAssignedToUser() throws SettingsException {
        User user = new User();
        user.setEmail("nobody333@example.com");

        Map<String, String> defaultSettings = userDefaultSettings.getUserDefaultSettings();

        // try to get first settings from the storage:
        Object[] keys = defaultSettings.keySet().toArray();
        String firstKey = keys[0].toString();

        // Object firstKey = Array.get(keys.toArray(), 0);
        settingsService.getValue(firstKey, user);

        // check that the class will call setter for settings
        verify(settingsService,times(defaultSettings.size())).setValue(any(), any(), any());
    }


}
