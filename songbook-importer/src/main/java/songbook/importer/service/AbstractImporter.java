package songbook.importer.service;

import org.springframework.beans.factory.annotation.Autowired;
import songbook.user.entity.User;
import songbook.user.repository.UserDao;
import java.util.Optional;

public abstract class AbstractImporter {
    @Autowired
    private UserDao userDao;

    public abstract void runImport();

    public User getDefaultUser() {
        long id = 1;
        Optional<User> user = userDao.findById(id);
        if(user.isPresent()) {
            System.out.println("Default user is found, getting by ID");
            return user.get();
        } else {
            System.out.println("Creating a new default user");
            return createDefaultUser();
        }
    }

    private User createDefaultUser() {
        User newUser = new User();
        newUser.setId(1);
        newUser.setEmail("nobody@example.com");
        userDao.save(newUser);
        return newUser;
    }
}
