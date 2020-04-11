package songbook.common;

import org.springframework.beans.factory.annotation.Autowired;
import songbook.user.entity.User;
import songbook.user.repository.UserDao;

public abstract class BaseIt {
    @Autowired
    protected UserDao userDao;

    protected User addUser(String email) {
        User user = new User().
                setEmail(email);

        userDao.save(user);

        return user;
    }

}
