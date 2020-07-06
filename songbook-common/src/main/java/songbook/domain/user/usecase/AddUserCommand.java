package songbook.domain.user.usecase;

import org.springframework.stereotype.Service;
import songbook.domain.user.entity.User;
import songbook.domain.user.port.out.PasswordEndoderPort;
import songbook.domain.user.port.out.SaveUserPort;

@Service
public class AddUserCommand implements songbook.domain.user.port.in.AddUserCommand {

    private final PasswordEndoderPort passwordEndoderPort;

    private final SaveUserPort saveUserPort;

    public AddUserCommand(PasswordEndoderPort passwordEndoderPort, SaveUserPort saveUserPort) {
        this.passwordEndoderPort = passwordEndoderPort;
        this.saveUserPort = saveUserPort;
    }

    @Override
    public User addUser(User user) {

        System.out.println("Ppp:" + user.getPassword() + ":" + passwordEndoderPort.encode(user.getPassword()));

        user.setPassword(passwordEndoderPort.encode(user.getPassword()));
        this.saveUserPort.saveUser(user);
        return user;
    }
}
