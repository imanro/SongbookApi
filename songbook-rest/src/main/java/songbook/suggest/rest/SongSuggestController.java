package songbook.suggest.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import songbook.song.entity.SongContentTypeEnum;
import songbook.suggest.entity.SongStatProj;
import songbook.suggest.repository.SongSuggestDao;
import songbook.suggest.view.Summary;
import songbook.user.entity.User;
import songbook.user.repository.UserDao;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/song-suggest")
public class SongSuggestController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SongSuggestDao songSuggestDao;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EntityManagerFactory emf;


    @GetMapping("/popular")
    @JsonView(Summary.class)
    Page<SongStatProj> findPopularSongs(Pageable pageable) {
        initFilters();
        return songSuggestDao.findPopularConcertItems(pageable);
    }

    private void initFilters() {
        User user = getDefaultUser();

        Session session = getSession();
        session.enableFilter("headerType").setParameter("type", SongContentTypeEnum.HEADER.toString());
        session.enableFilter("contentUser").setParameter("userId", user.getId());
    }


    private User getDefaultUser() {
        long defaultId = 1;
        return userDao.getOne(defaultId);
    }

    @Transactional
    private Session getSession() {
        return em.unwrap(Session.class);
    }
}
