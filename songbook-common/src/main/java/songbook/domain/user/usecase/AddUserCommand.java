package songbook.domain.user.usecase;

import org.springframework.stereotype.Service;
import songbook.domain.user.entity.User;
import songbook.domain.user.port.out.PasswordEndoderPort;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class AddUserCommand implements songbook.domain.user.port.in.AddUserCommand {

    private final PasswordEndoderPort passwordEndoderPort;

    public AddUserCommand(PasswordEndoderPort passwordEndoderPort) {
        this.passwordEndoderPort = passwordEndoderPort;
    }

    @Override
    public User addUser(User user) {

        user.setPassword(passwordEndoderPort.encode(user.getPassword()));
        return null;
    }
}
