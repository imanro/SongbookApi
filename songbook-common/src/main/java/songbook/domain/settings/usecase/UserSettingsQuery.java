package songbook.domain.settings.usecase;

import org.springframework.stereotype.Service;
import songbook.domain.user.entity.User;

import java.util.Map;

@Service
public class UserSettingsQuery {
    public Map<String, String> getSettingsByUser(User user) {
        return null;
    }
}
