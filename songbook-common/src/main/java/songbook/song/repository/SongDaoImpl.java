package songbook.song.repository;

import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;
import songbook.domain.song.entity.Song;
import songbook.domain.user.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Transactional
public class SongDaoImpl implements SongDaoCustom{
    @PersistenceContext
    protected EntityManager em;

    public Session getSession() {
        return em.unwrap(Session.class);
    }

    @Override
    // @Transactional
    public void refresh(Song song) {
        em.refresh(song);
    }

    @Override
    public void initContentUserFilter(User user) {
        this.getSession().enableFilter("contentUser").setParameter("userId", user.getId());
    }
}
