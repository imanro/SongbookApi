package songbook.settings.service;

import songbook.settings.entity.Setting;
import songbook.settings.repository.SettingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.user.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class Settings {

    @Autowired
    private SettingDao settingDao;

    private Map<Long, Map<String, String>> storage;

    public Settings() {
        this.storage = new HashMap<>();
    }

    public String getValue(String name, User user, String defaultValue) {
        if (!storage.containsKey(user.getId())) {
            fetchSettingsForUser(user);
        }

        if (storage.containsKey(user.getId())) {
            String value = storage.get(user.getId()).get(name);

            if (value != null) {
                return value;
            } else {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public String getValue(String name, User user) {
        if (!storage.containsKey(user.getId())) {
            fetchSettingsForUser(user);
        }

        if (storage.containsKey(user.getId())) {
            String value = storage.get(user.getId()).get(name);

            if (value != null) {
                return value;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    private void fetchSettingsForUser(User user) {
        Map<String, String> settings = settingDao.findAllByUser(user).stream().collect(Collectors.toMap(Setting::getName, Setting::getValue));
        storage.put(user.getId(), settings);
    }
}
