package songbook.domain.user.port.in;

import songbook.domain.user.entity.User;

public interface AddUserCommand {
    User addUser(User user);
}
