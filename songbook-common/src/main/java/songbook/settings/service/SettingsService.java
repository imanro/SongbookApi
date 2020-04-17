package songbook.settings.service;

import songbook.settings.configuration.UserDefaultSettings;
import songbook.settings.entity.Setting;
import songbook.settings.repository.SettingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import songbook.user.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SettingsService {

    @Autowired
    private SettingDao settingDao;

    @Autowired
    private UserDefaultSettings defaultSettings;

    private Map<Long, Map<String, String>> storage;

    public SettingsService() {
        this.storage = new HashMap<>();
    }

    public SettingsService setValue(String name, String value, User user) {
        Setting setting = new Setting()
                .setName(name)
                .setValue(value)
                .setUser(user);

        settingDao.save(setting);
        addStorageSetting(name, value, user);
        return this;
    }

    public String getValue(String name, User user, String defaultValue) throws SettingsException {
        if (!storage.containsKey(user.getId())) {
            // read database for this user
            Map<String, String> userSettings = fetchSettingsForUser(user);
            addStorageSettings(userSettings, user);
        }

        if (storage.containsKey(user.getId())) {
            String value = storage.get(user.getId()).get(name);

            if (value != null) {
                return value;

            } else {
                // one else attempt (may be, this is new setting that was not in the old database? store it)
                syncDefaultSettingsForUser(user);
                // but get default value
                return defaultValue;
            }
        } else {
            throw new SettingsException("The user settings cannot be get");
        }
    }

    public String getValue(String name, User user) throws SettingsException {
        if (!storage.containsKey(user.getId())) {
            Map<String, String> userSettings = fetchSettingsForUser(user);
            addStorageSettings(userSettings, user);
        }

        if (storage.containsKey(user.getId())) {
            String value = storage.get(user.getId()).get(name);

            if (value != null) {
                return value;
            } else {
                // one else attempt
                syncDefaultSettingsForUser(user);
                return storage.get(user.getId()).get(name);
            }
        } else {
            throw new SettingsException("The user settings cannot be get");
        }
    }

    public Map<String, String> getAllSettings(User user) throws SettingsException {
        if (!storage.containsKey(user.getId())) {
            Map<String, String> userSettings = fetchSettingsForUser(user);
            addStorageSettings(userSettings, user);
        }

        if (storage.containsKey(user.getId())) {
            return storage.get(user.getId());

        } else {
            throw new SettingsException("The user settings cannot be get");
        }
    }

    private void addStorageSetting(String key, String value, User user) {
        if(!storage.containsKey(user.getId())) {
            storage.put(user.getId(), new HashMap<>());
        }

        storage.get(user.getId()).put(key, value);
    }

    private void addStorageSettings(Map<String, String> settings, User user) {
        storage.put(user.getId(), settings);
    }


    private Map<String, String> fetchSettingsForUser(User user) {
        Map<String, String> settings = settingDao.findAllByUser(user).stream().collect(Collectors.toMap(Setting::getName, Setting::getValue));

        // if there is no settings, init with default values
        if(settings.size() == 0) {
            settings = initDefaultSettingsForUser(user);
        }

        return settings;
    }

    private Map<String, String> initDefaultSettingsForUser(User user) {
        Map<String, String> settings = defaultSettings.getUserDefaultSettings();

        for(Map.Entry<String, String> entry : settings.entrySet()) {
            setValue(entry.getKey(), entry.getValue(), user);
        }

        return settings;
    }

    private void syncDefaultSettingsForUser(User user) {
        Map<String, String> defaultSettings = this.defaultSettings.getUserDefaultSettings();
        Map<String, String> settings = settingDao.findAllByUser(user).stream().collect(Collectors.toMap(Setting::getName, Setting::getValue));

        // compare default settings vs. existing in db, if something missed, add
        for(Map.Entry<String, String> entry : defaultSettings.entrySet()) {
            if(!settings.containsKey(entry.getKey())) {
                setValue(entry.getKey(), entry.getValue(), user);
            }
        }
    }
}
