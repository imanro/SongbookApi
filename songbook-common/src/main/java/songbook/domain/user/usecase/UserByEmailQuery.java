package songbook.domain.user.usecase;

import org.springframework.stereotype.Service;
import songbook.domain.user.entity.User;
import songbook.domain.user.port.out.UserByEmailPort;

import java.util.Optional;

@Service
public class UserByEmailQuery implements songbook.domain.user.port.in.UserByEmailQuery {

    private final UserByEmailPort userByEmailPort;

    public UserByEmailQuery(UserByEmailPort userByEmailPort) {
        this.userByEmailPort = userByEmailPort;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return this.userByEmailPort.getUserByEmail(email);
    }
}
