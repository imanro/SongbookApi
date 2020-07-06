package songbook.adapter.user.out;

import org.springframework.stereotype.Component;
import songbook.domain.user.entity.User;
import songbook.domain.user.port.out.SaveUserPort;
import songbook.user.repository.UserDao;

@Component
public class SaveUserRdbmAdapter implements SaveUserPort {

    private final UserDao userDao;

    public SaveUserRdbmAdapter(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User saveUser(User user) {
        return userDao.save(user);
    }
}
