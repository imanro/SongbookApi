package songbook.domain.user.port.out;

import songbook.domain.user.entity.User;

public interface SaveUserPort {
    User saveUser(User user);
}
