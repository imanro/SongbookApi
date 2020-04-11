package songbook.settings.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import songbook.common.BaseIt;
import songbook.settings.entity.Setting;
import songbook.settings.repository.SettingDao;
import songbook.user.entity.User;
import org.junit.jupiter.api.Assertions;

@SpringBootTest
public class SettingsTest extends BaseIt  {

    @Autowired
    private SettingDao settingsDao;

    @Autowired
    private Settings settings;

    @BeforeEach
    void cleanAll() {
        settingsDao.deleteAll();
        userDao.deleteAll();
    }

    @Test
    void settingCanBeRead() {
        // init user through dao
        User user = addUser("nobody@example.com");
        User strangerUser = addUser("nobody2@example.com");

        String dirNameValue = "My directory";
        String dirNameValueStranger = "My directory";
        String serverNameValue = "My server";

        // init settings values through dao
        addSetting("dirName", dirNameValue, user);
        addSetting("serverName", serverNameValue, user);
        addSetting("dirNameStranger", dirNameValueStranger, strangerUser);

        // check that we can obtain necessary values
        Assertions.assertEquals(dirNameValue, settings.getValue("dirName", user));
        Assertions.assertEquals(serverNameValue, settings.getValue("serverName", user));
    }

    @Test
    void canFallBackToDefaultSettingValue() {
        // init user through dao
        User user = addUser("nobody@example.com");
        User strangerUser = addUser("nobody2@example.com");

        String dirNameValue = "My directory";
        String serverNameValue = "My server";

        // init settings values through dao
        addSetting("dirName", dirNameValue, user);
        addSetting("serverName", serverNameValue, user);

        // check that we can obtain necessary values

        // value dont exist for user, though other user settings were stored
        Assertions.assertEquals("defaultValueDirName", settings.getValue("dirNameSss", user, "defaultValueDirName"));
        Assertions.assertEquals("defaultValueServerName", settings.getValue("serverNameGgg", strangerUser, "defaultValueServerName"));
    }

    private Setting addSetting(String name, String value, User user) {
        Setting setting = new Setting()
                .setName(name)
                .setValue(value)
                .setUser(user);

        settingsDao.save(setting);
        return setting;
    }

}
