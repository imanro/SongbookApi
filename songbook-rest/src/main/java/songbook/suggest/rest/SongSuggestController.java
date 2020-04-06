package songbook.suggest.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import songbook.concert.entity.Concert;
import songbook.concert.repository.ConcertDao;
import songbook.song.entity.SongContentTypeEnum;
import songbook.suggest.entity.SongProj;
import songbook.suggest.entity.SongStat;
import songbook.suggest.entity.SongStatProj;
import songbook.suggest.repository.SongSuggestDao;
import songbook.suggest.service.SongStatService;
import songbook.suggest.view.Summary;
import songbook.user.entity.User;
import songbook.user.repository.UserDao;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/song-suggest")
public class SongSuggestController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SongSuggestDao songSuggestDao;

    @Autowired
    private ConcertDao concertDao;

    @Autowired
    private SongStatService songStatService;


    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EntityManagerFactory emf;


    @GetMapping("/popular")
    @JsonView(Summary.class)
    Page<SongStatProj> findPopularSongs(Pageable pageable) {
        initFilters();
        Page<SongStatProj> items = songSuggestDao.findPopularConcertItems(pageable);

        // hack: attaching concert entities manually
        List<Long> concertIds = songStatService.extractConcertIds(items);
        Page<SongStatProj> newItems;
        if(concertIds.size() > 0) {
            List<Concert> concerts = concertDao.findAllById(concertIds);
            newItems = songStatService.attachConcertsToStat(items, concerts, pageable);
        } else {
            newItems = items;
        }

        return newItems;
    }

    @GetMapping("/recent/{fromDate}")
    @JsonView(Summary.class)
    Page<SongProj> findRecentSongs(
            @PathVariable("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            Pageable pageable
    ) {
        initFilters();
        return songSuggestDao.findRecentSongs(pageable, convertToDate(fromDate));
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

    private Date convertToDate(LocalDate localDate) {
        return Date.from( localDate.atStartOfDay( ZoneId.systemDefault()).toInstant());
    }

}
