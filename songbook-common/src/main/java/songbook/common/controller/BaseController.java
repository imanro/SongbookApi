package songbook.common.controller;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import songbook.domain.user.entity.User;
import songbook.user.repository.UserDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

public class BaseController {
    @PersistenceContext
    protected EntityManager em;

    @Autowired
    protected UserDao userDao;

    @Transactional
    protected Session getSession() {
        return em.unwrap(Session.class);
    }

    /**
     * Default method whilst we don't have full multiuser system
     */
    protected User getDefaultUser() {
        long defaultId = 1;
        return userDao.getOne(defaultId);
    }

}
