package songbook.domain.user.port.out;


import songbook.domain.user.entity.User;

import java.util.Optional;

public interface UserByEmailPort {
    Optional<User> getUserByEmail(String email);
}
