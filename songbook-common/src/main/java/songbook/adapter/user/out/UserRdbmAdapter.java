package songbook.adapter.user.out;

import org.springframework.stereotype.Component;
import songbook.domain.user.entity.User;
import songbook.domain.user.port.out.SaveUserPort;
import songbook.domain.user.port.out.UserByEmailPort;
import songbook.user.repository.UserDao;

import java.util.Optional;

@Component
public class UserRdbmAdapter implements SaveUserPort, UserByEmailPort {

    private final UserDao userDao;

    public UserRdbmAdapter(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User saveUser(User user) {
        return userDao.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
       return userDao.findByEmail(email);
    }
}
