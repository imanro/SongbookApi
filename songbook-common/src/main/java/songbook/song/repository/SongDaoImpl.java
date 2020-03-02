package songbook.song.repository;

import org.springframework.transaction.annotation.Transactional;
import songbook.song.entity.Song;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class SongDaoImpl implements SongDaoCustom{
    @PersistenceContext
    private EntityManager em;
    @Override
    @Transactional
    public void refresh(Song song) {
        em.refresh(song);
    }
}
