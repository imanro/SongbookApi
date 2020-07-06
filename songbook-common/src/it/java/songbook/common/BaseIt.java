package songbook.common;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import songbook.domain.user.entity.User;
import songbook.user.repository.UserDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class BaseIt {

    // @PersistenceContext
    // protected EntityManager em;

    @Autowired
    protected EntityManagerFactory emf;

    protected EntityManager em2;

    @Autowired
    protected UserDao userDao;

    // @Transactional
    protected Session getSession() {
        if(em2 == null) {
            em2 = emf.createEntityManager();
        }

        return em2.unwrap(Session.class);
    }

    protected User addUser(String email) {
        User user = new User().
                setEmail(email);

        userDao.save(user);

        return user;
    }

    protected Date parseDateString(String dateString) {
        LocalDateTime localDate = LocalDateTime.parse(
                dateString,
                DateTimeFormatter.ofPattern( "uuuu-MM-dd HH:mm:ss" )
        );

        return Date.from( localDate.atZone( ZoneId.systemDefault()).toInstant());
    }

}
