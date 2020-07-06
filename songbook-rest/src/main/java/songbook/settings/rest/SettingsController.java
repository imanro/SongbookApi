package songbook.settings.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import songbook.common.controller.BaseController;
import songbook.settings.service.SettingsException;
import songbook.settings.service.SettingsService;
import songbook.domain.user.entity.User;

import java.util.Map;

@RestController
@RequestMapping("/settings")
public class SettingsController extends BaseController {

    @Autowired
    SettingsService settingsService;

    @GetMapping("")
    Map<String, String> findAllByUser() {
        User user = getDefaultUser();
        Map<String, String> settings;

        try {
            settings = settingsService.getAllSettings(user);
        } catch(SettingsException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to get user settings: " + e.getMessage());
        }

        return settings;
    }
}
