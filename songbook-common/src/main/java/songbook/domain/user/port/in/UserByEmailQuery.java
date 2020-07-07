package songbook.domain.user.port.in;

import songbook.domain.user.entity.User;

import java.util.Optional;

public interface UserByEmailQuery {
    Optional<User> getUserByEmail(String email);
}
